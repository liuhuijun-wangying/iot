package com.iot.client.netty;

import com.google.protobuf.ByteString;
import com.iot.client.ClientEnv;
import com.iot.common.constant.Cmds;
import com.iot.common.model.BaseMsg;
import com.iot.common.util.CryptUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by zc on 16-9-1.
 */
public abstract class AbstractHandler extends SimpleChannelInboundHandler<BaseMsg.BaseMsgPb> {

    protected static BaseMsg.BaseMsgPb.Builder HEARTBEAT_MSG = BaseMsg.BaseMsgPb.newBuilder().setCmd(Cmds.CMD_HEARTBEAT);

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
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("----channelInactive----");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(IdleStateEvent.class.isAssignableFrom(evt.getClass())){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.ALL_IDLE){
                onIdle(ctx);
            }
        }
    }

    protected abstract void onIdle(ChannelHandlerContext ctx);
}
