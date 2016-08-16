package com.iot.tcpserver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iot.common.constant.Cmds;
import com.iot.common.kafka.KafkaMsg;
import com.iot.common.kafka.BaseKafkaConsumer;
import com.iot.common.util.TextUtil;
import com.iot.tcpserver.client.ClientManager;
import com.iot.tcpserver.codec.BaseMsg;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zc on 16-8-8.
 */
public class ServiceRespHandler implements BaseKafkaConsumer.KafkaProcessor{

    @Override
    public void process(String topic, Short key, KafkaMsg value) {
        //System.err.println("======>topic:"+topic+",key:"+key+",value:"+new String(value.getData()));
        if(key==null || value==null){
            return;
        }

        //不是此server
        ChannelHandlerContext ctx = ClientManager.getInstance().getContext(value.getChannelId());
        if(ctx==null){
            return;
        }

        switch (key){
            case Cmds.CMD_APP_AUTH:
                doAppAuth(value,ctx);
                break;
            case Cmds.CMD_APP_REGISTER:
                doAppRegist(value,ctx);
                break;
        }
    }

    private void doAppAuth(KafkaMsg value, ChannelHandlerContext ctx){
        if(TextUtil.isEmpty(value.getData())){
            return;
        }

        try {
            JSONObject json = JSON.parseObject(new String(value.getData(),"UTF-8"));
            byte statusCode = json.getByteValue("code");
            if(statusCode==1){//login ok
                String clientId = json.getString("id");
                if(TextUtil.isEmpty(clientId)){//id is null
                    ctx.writeAndFlush(new BaseMsg(Cmds.CMD_APP_AUTH,value.getMsgId(),new byte[]{3}));
                    return;
                }
                String oldId = ctx.channel().attr(ServerEnv.ID).get();
                if(oldId!=null){//has authed
                    ctx.writeAndFlush(new BaseMsg(Cmds.CMD_APP_AUTH,value.getMsgId(),new byte[]{5}));
                    return;
                }
                String username = json.getString("username");
                ctx.channel().attr(ServerEnv.ID).set(clientId);
                ctx.channel().attr(ServerEnv.VERSION).set(json.getString("version"));
                ctx.channel().attr(ServerEnv.TYPE).set(ServerEnv.CLIENT_TYPE_APP);
                ctx.channel().attr(ServerEnv.USERNAME).set(username);
                ClientManager.getInstance().onLogin(username,ctx);
                //ok
                ctx.writeAndFlush(new BaseMsg(Cmds.CMD_APP_AUTH,value.getMsgId(),new byte[]{1}));
            }else{
                ctx.writeAndFlush(new BaseMsg(Cmds.CMD_APP_AUTH,value.getMsgId(),new byte[]{statusCode}));
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.writeAndFlush(new BaseMsg(Cmds.CMD_APP_AUTH,value.getMsgId(),new byte[]{4}));
        }
    }

    private void doAppRegist(KafkaMsg value,ChannelHandlerContext ctx){
        if(TextUtil.isEmpty(value.getData())){
            return;
        }
        try {
            JSONObject json = JSON.parseObject(new String(value.getData(),"UTF-8"));
            byte statusCode = json.getByteValue("code");
            ctx.writeAndFlush(new BaseMsg(Cmds.CMD_APP_REGISTER,value.getMsgId(),new byte[]{statusCode}));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.writeAndFlush(new BaseMsg(Cmds.CMD_APP_REGISTER,value.getMsgId(),new byte[]{4}));
        }
    }
}
