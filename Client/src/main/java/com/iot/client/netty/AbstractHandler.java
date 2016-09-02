package com.iot.client.netty;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.iot.client.ClientEnv;
import com.iot.common.constant.Cmds;
import com.iot.common.constant.RespCode;
import com.iot.common.model.BaseMsg;
import com.iot.common.util.CryptUtil;
import com.iot.common.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by zc on 16-9-1.
 */
public abstract class AbstractHandler extends SimpleChannelInboundHandler<BaseMsg.BaseMsgPb> {

    protected static BaseMsg.BaseMsgPb.Builder HEARTBEAT_MSG = BaseMsg.BaseMsgPb.newBuilder().setCmd(Cmds.CMD_HEARTBEAT);
    private int heartbeatCount;

    protected void sendAesKey(ChannelHandlerContext ctx, BaseMsg.BaseMsgPbOrBuilder msg) throws Exception {
        byte[] b = CryptUtil.rsaEncryptByPublicKey(ClientEnv.AES_KEY,CryptUtil.bytes2PublicKey(msg.getData().toByteArray()));
        BaseMsg.BaseMsgPb.Builder builder = BaseMsg.BaseMsgPb.newBuilder();
        builder.setCmd(Cmds.CMD_SEND_AES_KEY);
        builder.setData(ByteString.copyFrom(b));
        ctx.writeAndFlush(builder);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("----exceptionCaught----:"+cause.getMessage());
        //TODO reconnect
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("----channelInactive----");
        //TODO
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("----channelActive----");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(IdleStateEvent.class.isAssignableFrom(evt.getClass())){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.ALL_IDLE){
                onIdle(ctx);
                heartbeatCount++;
                if ((heartbeatCount*ClientEnv.IDLE_TIME)>=120){//2min
                    //TODO reconnect;
                }
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMsg.BaseMsgPb msg) throws Exception {
        heartbeatCount = 0;
        switch (msg.getCmd()) {
            case Cmds.CMD_PUSH_RSA_PUB_KEY:
                sendAesKey(ctx, msg);
                break;
            case Cmds.CMD_SEND_AES_KEY://resp
                onDiscussKeyResp(ctx, msg);
                break;
            case Cmds.CMD_ANOTHOR_LOGIN:
                System.err.println("ctx is closed due to another login");
                //TODO kill process
                break;
            case Cmds.CMD_EXP:
                System.err.println("ctx is reconnecting due to server internal exp");
                //TODO
                break;
        }
        onRead(ctx,msg);
    }
    private void onDiscussKeyResp(ChannelHandlerContext ctx, BaseMsg.BaseMsgPbOrBuilder msg) {
        if(msg.getData().isEmpty()){
            System.err.println("=====>discuss key resp msg is empty");
            return;
        }

        JSONObject json = JsonUtil.bytes2Json(msg.getData().toByteArray());
        int statusCode = json.getIntValue("code");

        if(statusCode== RespCode.COMMON_OK){
            System.out.println("=====>discuss key ok");
            onDiscussKeyOk(ctx);
        }else{
            System.err.println("=====>discuss key failed, err code::"+statusCode);
        }
    }

    protected abstract void onIdle(ChannelHandlerContext ctx);
    protected abstract void onRead(ChannelHandlerContext ctx, BaseMsg.BaseMsgPb msg)throws Exception;
    protected abstract void onDiscussKeyOk(ChannelHandlerContext ctx);
}
