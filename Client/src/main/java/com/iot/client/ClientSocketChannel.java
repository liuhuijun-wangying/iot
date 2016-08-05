package com.iot.client;

import com.iot.client.codec.BaseMsg;
import com.iot.client.codec.ClientPipeline;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientSocketChannel {

	/** status */
	private static final int  STATUS_IDLE = 0;
	private static final int  STATUS_CONNECTING = 1;
	private static final int  STATUS_CONNECTED = 2;
	private static final int  STATUS_RECONNECTING = 3;

	/**  cmd */
	private static final int CONNECT = 0;
	private static final int DISCONNECT = 1;
	private static final int SEND = 2;
	private static final int RECONNECT = 3;

	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private volatile int status = STATUS_IDLE;

	private Selector selector;
	private SocketChannel sc;
	private List<ByteBuffer> writeCache = new ArrayList<>();
	private ByteBuffer buf = ByteBuffer.allocate(2*1024);//2k
	private BlockingQueue<Cmd> cmdList = new LinkedBlockingQueue<>();

	private String ip;
	private int port;

	private long startConnTime;
	private long sendOrRecvDataTime;
	private long startReconnTime;
	private int reconnRetryCount;
	private int heartbeatCount;

	private ClientPipeline pipeline = new ClientPipeline();

	private ChannelHandler<ClientSocketChannel,BaseMsg> handler;
	public void setHandler(ChannelHandler<ClientSocketChannel,BaseMsg> handler){
		this.handler = handler;
	}
	
	public ClientSocketChannel(String ip, int port){
		if(ip==null || "".equals(ip.trim()) || port<=0 || port > 65535){
			throw new IllegalArgumentException();
		}
		this.ip = ip;
		this.port = port;
	}

	private int idleTime = 30;
	public void setIdleTimeSecond(int second){
		if(second<=0){
			throw new IllegalArgumentException();
		}
		this.idleTime = second;
	}

	//api
	public void send(BaseMsg obj) {//api
		byte[] encoded = null;
		try {
			encoded = pipeline.encode(obj);
		}catch (Exception e){
			e.printStackTrace();
			//TODO handle error
		}

		if(encoded!=null){
			addCmd(new Cmd(SEND,encoded));
		}
	}

	/*public void stop() {
		isRunning.compareAndSet(true, false);
		if (selector != null && selector.isOpen()) {
			selector.wakeup();
		}
	}

	public void disconnect() {
		addCmd(new Cmd(DISCONNECT));
	}*/

	private void doCmd(){

		while (!cmdList.isEmpty()) {
			Cmd cmd = cmdList.poll();
			switch (cmd.what) {
				case CONNECT:
					doCmdConnect();
					break;
				case DISCONNECT:
					if (status != STATUS_IDLE) {
						logI("doCmd():disconnecting....");
						closeSocket();
					}
					break;
				case SEND:
					if (status == STATUS_CONNECTED) {
						sendOrRecvDataTime = System.nanoTime()/1000000;
						doSend(cmd.data, sc, writeCache);
					}
					break;
				case RECONNECT:
					if(status != STATUS_IDLE && status != STATUS_RECONNECTING){
						logI("doCmd():reconnecting....");
						startReconnTime = System.nanoTime()/1000000;
						status = STATUS_RECONNECTING;
					}
			}
		}
	}

	private void doCmdConnect(){
		if (status == STATUS_IDLE) {
			status = STATUS_CONNECTING;
			logI("--connecting--");
			startConnTime = System.nanoTime()/1000000;
			try {
				sc = SocketChannel.open();
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_CONNECT);
				try {
					if(sc.connect(new InetSocketAddress(ip,port))){
						onConnected();
					}
				} catch (Exception e) {//include dns resolve exception
					logE("sc.connect() IOE");
					reconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
				closeSocket();
			}
		}
	}

	public int start(){
		if(!isRunning.compareAndSet(false, true)){
			throw new IllegalStateException("has started");
		}
		
		if (selector == null || !selector.isOpen()) {
			try {
				selector = Selector.open();
			} catch (IOException e) {
				logE("selector open error::"+e.getMessage());
				isRunning.set(false);
				return -1;
			}
		}

		logI("open selector successful");

		addCmd(new Cmd(CONNECT));
		while (isRunning.get()) {
			doCmd();
			if (STATUS_IDLE == status) {
				try {
					selector.select();
				} catch (IOException e) {
					logE("select IOE when status = IDLE");
					isRunning.set(false);
					return -1;
				}
			} else if (STATUS_CONNECTING == status) {
				try {
					selector.select(15 * 1000);
				} catch (IOException e) {
					logE("select IOE when status = CONNECTING");
					isRunning.set(false);
					return -1;
				}
				try {
					doStatusConnecting();
				} catch (ClosedChannelException e) {
					logE("Socket Reg Error when conn ok");
					isRunning.set(false);
					return -1;
				}
			} if (STATUS_CONNECTED == status) {
				try {
					selector.select(idleTime * 1000);
				} catch (IOException e1) {
					logE("Selector Select Error when status = CONNECTED");
					isRunning.set(false);
					return -1;
				}
				doStatusConnected();
			}else if(STATUS_RECONNECTING == status){//每重试连接失败一次，间隔时间就加1s
				try {
					selector.select((5+reconnRetryCount)*1000);
				} catch (IOException e) {
					logE("Selector Select Error when status = RECONNECTING");
					isRunning.set(false);
					return -1;
				}
				doStatusReconnecting();
			}
		}//quit while
		
		closeSocket();
		closeSelector();
		clear();
		isRunning.set(false);
		return 0;
	}

	private void doStatusConnecting() throws ClosedChannelException {
		Set<SelectionKey> set = selector.selectedKeys();
		if (set.isEmpty()) {
			if ((System.nanoTime()/1000000 - startConnTime) >= 15 * 1000) {
				logE("connect time out---");
				reconnect();
			}
		} else {
			set.clear();
			if (sc.isConnectionPending()) {
				boolean isFinishedConn = false;
				try {
					isFinishedConn = sc.finishConnect();
				} catch (IOException e) {//refuse
					//e.printStackTrace();
					logE("finishConnect IOE");
					reconnect();
				}
				if(isFinishedConn){
					onConnected();
				}else{
					logE("sc.finishConnect() == false");
					reconnect();
				}
			} else {
				logE("sc.isConnectionPending() == false");
				reconnect();
			}
		}
	}

	private void doStatusReconnecting(){
		Set<SelectionKey> set = selector.selectedKeys();
		set.clear();

		if ((System.nanoTime()/1000000 - startReconnTime) >= (5+reconnRetryCount)*1000) {
			logI("----RECONNECTING----time:"+System.currentTimeMillis()/1000+"s");
			reconnRetryCount++;
			closeSocket();
			addCmd(new Cmd(CONNECT));
		}
	}

	private void doStatusConnected(){
		Set<SelectionKey> set = selector.selectedKeys();
		if (!set.isEmpty()) {
			Iterator<SelectionKey> it = set.iterator();
			SelectionKey mKey = it.next();
			if(mKey.isReadable()){
				doRead();
			}else if(mKey.isWritable()){
				try {
					doWrite(sc,writeCache);
				} catch (IOException e) {
					reconnect();
					logE("write error");
				}
			}
			set.clear();
		}

		if((System.nanoTime()/1000000-sendOrRecvDataTime) >= idleTime*1000){
			if(handler!=null){
				handler.onIdle(this);
			}
			//send +1 , recv clear
			if(heartbeatCount>3){
				logE("no heartbeat count::"+heartbeatCount);
				reconnect();
			}else{
				heartbeatCount ++;
			}
		}
	}

	private void onConnected() throws ClosedChannelException{
		reconnRetryCount = 0;
		sc.register(selector, SelectionKey.OP_READ);
		status = STATUS_CONNECTED;
		heartbeatCount = 0;
		sendOrRecvDataTime = System.nanoTime()/1000000;
		logI("connect ok---");
		if(handler!=null){
			handler.onConnected(this);
		}
	}

	private void reconnect(){
		addCmd(new Cmd(RECONNECT));
	}

	private void closeSocket() {
		status = STATUS_IDLE;
		if(sc ==  null){
			return;
		}
		if(handler!=null){
			handler.onClosed();
		}

		if (selector != null) {
			SelectionKey key = sc.keyFor(selector);
			if (key != null) {
				key.cancel();
			}
		}

		try {
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sc = null;
	}

	private void closeSelector(){
		if (selector != null) {
			if (selector.isOpen()) {
				try {
					selector.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			selector = null;
		}
	}

	private void clear(){
		cmdList.clear();
		buf.clear();
		writeCache.clear();
	}

	private void doRead(){
		buf.clear();
		try {
			int count = sc.read(buf);
			if (-1 == count && buf.position() == 0) {
				logE("reset by peer when read ");
				reconnect();
			} else if (count>0){
				sendOrRecvDataTime = System.nanoTime()/1000000;
				heartbeatCount = 0;
				List<BaseMsg> msg = pipeline.decode(Arrays.copyOfRange(buf.array(), 0, count));
				if(handler!=null && !msg.isEmpty()){
					for(BaseMsg obj: msg){
						handler.onRead(this,obj);
					}
				}
				buf.clear();
			}
		} catch (IOException e1) {
			logE("read IOE");
			reconnect();
		} catch (Exception e) {
			e.printStackTrace();
			//TODO handle error
		}
	}

	private void doWrite(SocketChannel sc, List<ByteBuffer> writeCache) throws IOException{
		if(writeCache.isEmpty()){
			sc.register(selector, SelectionKey.OP_READ);
			return;
		}
		Iterator<ByteBuffer> it = writeCache.iterator();
		ByteBuffer buf;
		int ret = 0;
		int remaining = 0;
		while(it.hasNext()){
			buf = it.next();
			remaining = buf.remaining();
			ret = sc.write(buf);
			if(ret != remaining){
				//如果还是没发完就不remove，但是不能继续发了，否则顺序出错
				//这样下次还会重发，并且发的时候长度不会出错
				//因为ByteBuffer的pos已经变化了，还在writeList里面存着
				logE("doWrite(), ret!=remaining,ret=="+ret
						+",remaining=="+remaining);
				break;
			}
			it.remove();
		}
		if(ret == remaining){
			sc.register(selector, SelectionKey.OP_READ);
		}
	}

	private void doSend(byte[] data, SocketChannel sc, List<ByteBuffer> writeCache){
		ByteBuffer buf = ByteBuffer.wrap(data);

		if(!writeCache.isEmpty() || sc==null){
			writeCache.add(buf);
			try {
				sc.register(selector, SelectionKey.OP_WRITE);
			} catch (ClosedChannelException e) {
				e.printStackTrace();
			}
			return;
		}

		try {
			int ret = sc.write(buf);
			if(ret!=data.length){
				//write不会返回-1
				logE("ret == "+ret+",remaing=="+buf.remaining()+" when write");
				//不用截取buf未发送的剩余的部分，write方法会从ByteBuffer的pos位置发送
				writeCache.add(buf);
				sc.register(selector, SelectionKey.OP_WRITE);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class Cmd {
		public int what;
		public byte[] data;

		public Cmd(int what) {
			this.what = what;
		}

		public Cmd(int what, byte[] data) {
			this.what = what;
			this.data = data;
		}
	}
	
	private void addCmd(Cmd cmd) {
		boolean b = cmdList.offer(cmd);
		if (!b) {
			logE("--cmdList is full--");
			return;
		}
		if (selector != null && selector.isOpen()) {
			selector.wakeup();
		}
	}

	//方便不同平台更换log工具,比如android/log4j/sdout...
	private void logE(String msg){
		System.out.println(msg);
	}

	private void logI(String msg){
		System.out.println(msg);
	}

}
