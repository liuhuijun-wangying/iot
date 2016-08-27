package com.iot.tcpserver;

import com.google.protobuf.*;
import com.iot.common.model.BaseMsg;
import com.iot.common.util.CryptUtil;
import com.iot.common.util.TextUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

public class TcpServer {

    private static Logger log = LoggerFactory.getLogger(TcpServer.class);

    public static void startNetty(int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(handler);
            b.option(ChannelOption.SO_BACKLOG, 1024);

            ChannelFuture f = b.bind(port).sync();
            log.info("tcp server has started on port "+port);
            f.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private static final ChannelHandler handler = new ChannelInitializer<SocketChannel>() {
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
            ch.pipeline().addLast(new MyProtobufDecoder());
            ch.pipeline().addLast(new LengthFieldPrepender(4, false));
            ch.pipeline().addLast(new MyProtobufEncoder());
            ch.pipeline().addLast(new TcpServerHandler());
        }
    };

    private static class MyProtobufDecoder extends MessageToMessageDecoder<ByteBuf> {

        private final BaseMsg.BaseMsgPb prototype = BaseMsg.BaseMsgPb.getDefaultInstance();

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
                throws Exception {
            final byte[] array;
            final int offset;
            final int length = msg.readableBytes();
            if (msg.hasArray()) {
                array = msg.array();
                offset = msg.arrayOffset() + msg.readerIndex();
            } else {
                array = new byte[length];
                msg.getBytes(msg.readerIndex(), array, 0, length);
                offset = 0;
            }

            BaseMsg.BaseMsgPb result = prototype.getParserForType().parseFrom(array, offset, length);
            if (result.getIsEncrypt()){
                byte[] aesKey = ctx.channel().attr(ServerEnv.KEY).get();
                if(TextUtil.isEmpty(aesKey)){
                    log.error("aes key is null");
                    ctx.close();
                    return;
                }
                if(!result.getData().isEmpty()){
                    byte[] decryptedData = CryptUtil.aesDecrypt(result.getData().toByteArray(),aesKey);
                    out.add(result.toBuilder().setData(ByteString.copyFrom(decryptedData)).build());
                    return;
                }
            }
            out.add(result);
        }
    }


    private static class MyProtobufEncoder extends MessageToMessageEncoder<BaseMsg.BaseMsgPbOrBuilder> {
        @Override
        protected void encode(ChannelHandlerContext ctx, BaseMsg.BaseMsgPbOrBuilder msg, List<Object> out)
                throws Exception {

            BaseMsg.BaseMsgPb.Builder result = null;
            if (msg instanceof BaseMsg.BaseMsgPb){
                result = ((BaseMsg.BaseMsgPb)msg).toBuilder();
            }else if (msg instanceof BaseMsg.BaseMsgPb.Builder){
                result = (BaseMsg.BaseMsgPb.Builder)msg;
            }

            if (result.getIsEncrypt()){
                byte[] aesKey = ctx.channel().attr(ServerEnv.KEY).get();
                if(TextUtil.isEmpty(aesKey)){
                    log.error("aes key is null");
                    ctx.close();
                    return;
                }
                if(!result.getData().isEmpty()){
                    byte[] encryptedData = CryptUtil.aesEncrypt(result.getData().toByteArray(), aesKey);
                    result.setData(ByteString.copyFrom(encryptedData));
                }
            }
            out.add(wrappedBuffer(result.build().toByteArray()));
        }
    }

}
