package com.iot.client.netty;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.iot.client.ClientEnv;
import com.iot.common.constant.Cmds;
import com.iot.common.constant.RespCode;
import com.iot.common.model.BaseMsg;
import com.iot.common.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zc on 16-9-1.
 */
public class DeviceHandler extends AbstractHandler {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMsg.BaseMsgPb msg) throws Exception {
        switch (msg.getCmd()){
            case Cmds.CMD_PUSH_RSA_PUB_KEY:
                sendAesKey(ctx,msg);
                break;
            case Cmds.CMD_SEND_AES_KEY://resp
                onDiscussKeyResp(ctx,msg);
                break;
            case Cmds.CMD_DEVICE_AUTH://resp
                onDeviceAuthResp(msg,ctx);
                break;
            case Cmds.CMD_ANOTHOR_LOGIN:
                System.err.println("ctx is closed due to another login");
                ctx.disconnect();
                break;
            case Cmds.CMD_EXP:
                System.err.println("ctx is closed due to server internal exp");
                ctx.disconnect();
                break;
            case Cmds.CMD_IM_PUSH:
                omImPush(ctx,msg);
                break;
        }
    }
    private void omImPush(ChannelHandlerContext ctx, BaseMsg.BaseMsgPbOrBuilder msg){
        if(msg.getData().isEmpty()){
            System.err.println("=====>omImPush msg is empty,msgid="+msg.getMsgId());
            return;
        }
        JSONArray jsonArray = JsonUtil.bytes2JsonArray(msg.getData().toByteArray());
        if (jsonArray.size()==0){
            System.err.println("recv im push size=0");
            return;
        }

        //resp
        BaseMsg.BaseMsgPb.Builder builder = BaseMsg.BaseMsgPb.newBuilder();
        builder.setCmd(Cmds.CMD_IM_PUSH);
        JSONArray resp = new JSONArray();
        for (int i=0;i<jsonArray.size();i++){
            System.out.println("=====>recv im,msgid="+msg.getMsgId()+",json="+jsonArray.getJSONObject(i).toJSONString());
            JSONObject respJson = new JSONObject();
            respJson.put("msgid",jsonArray.getJSONObject(i).getString("msgid"));
            resp.add(respJson);
        }
        builder.setData(ByteString.copyFrom(JsonUtil.json2Bytes(resp)));
        ctx.writeAndFlush(builder);
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
            doDeviceAuth(ctx);
        }else{
            System.err.println("=====>discuss key failed, err code::"+statusCode);
        }
    }

    private void onDeviceAuthResp(BaseMsg.BaseMsgPbOrBuilder msg, ChannelHandlerContext ctx) {
        if(msg.getData().isEmpty()){
            System.err.println("=====>app auth resp msg is empty");
            return;
        }

        JSONObject json = JsonUtil.bytes2Json(msg.getData().toByteArray());
        int statusCode = json.getIntValue("code");

        if(statusCode== RespCode.COMMON_OK){
            System.out.println("=====>auth ok");
            BaseMsg.BaseMsgPb.Builder builder = BaseMsg.BaseMsgPb.newBuilder();
            builder.setCmd(Cmds.CMD_GET_IM_OFFLINE_MSG);
            ctx.writeAndFlush(builder);
        }else{
            System.err.println("=====>onDiscussKeyResp::errCode::"+statusCode);
        }
    }

    private void doDeviceAuth(ChannelHandlerContext ctx) {
        JSONObject json = new JSONObject();
        json.put("version","1.0");
        json.put("id", ClientEnv.CLIENT_ID);
        JSONArray jsonArray = new JSONArray();
        //add some abilities
        jsonArray.add("camera");
        jsonArray.add("fly");// -_-
        json.put("abilities",jsonArray);

        BaseMsg.BaseMsgPb.Builder builder = BaseMsg.BaseMsgPb.newBuilder();
        builder.setCmd(Cmds.CMD_DEVICE_AUTH);
        builder.setIsEncrypt(true);
        builder.setData(ByteString.copyFrom(JsonUtil.json2Bytes(json)));
        ctx.writeAndFlush(builder);
    }

    @Override
    protected void onIdle(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(HEARTBEAT_MSG);
    }
}
