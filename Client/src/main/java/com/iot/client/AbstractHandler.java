package com.iot.client;

import com.google.protobuf.ByteString;
import com.iot.common.constant.Cmds;
import com.iot.common.model.BaseMsg;
import com.iot.common.util.CryptUtil;

/**
 * Created by zc on 16-8-27.
 */
public class AbstractHandler implements ChannelHandler<ClientSocketChannel,BaseMsg.BaseMsgPbOrBuilder>  {

    protected static BaseMsg.BaseMsgPb.Builder HEARTBEAT_MSG;

    public AbstractHandler(){
        HEARTBEAT_MSG = BaseMsg.BaseMsgPb.newBuilder();
        HEARTBEAT_MSG.setCmd(Cmds.CMD_HEARTBEAT);
    }

    @Override
    public void onConnected(ClientSocketChannel ctx) {
        System.out.println("----onConnected----");
        //对于一些没有RSA计算能力的设备,可以不进行密钥协商,直接doAuth
        /*try {
            doDeviceAuth(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onClosed() {
        System.out.println("----onClosed----");
    }

    @Override
    public void onRead(ClientSocketChannel ctx, BaseMsg.BaseMsgPbOrBuilder msg) throws Exception {

    }

    @Override
    public void onIdle(ClientSocketChannel ctx) {//send heartbeat pack
        ctx.send(HEARTBEAT_MSG);
    }

    protected void sendAesKey(ClientSocketChannel ctx, BaseMsg.BaseMsgPbOrBuilder msg) throws Exception {
        byte[] b = CryptUtil.rsaEncryptByPublicKey(ClientEnv.AES_KEY,CryptUtil.bytes2PublicKey(msg.getData().toByteArray()));
        BaseMsg.BaseMsgPb.Builder builder = BaseMsg.BaseMsgPb.newBuilder();
        builder.setCmd(Cmds.CMD_SEND_AES_KEY);
        builder.setData(ByteString.copyFrom(b));
        ctx.send(builder);
    }
}
