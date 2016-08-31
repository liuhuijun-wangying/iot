package com.iot.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.iot.common.constant.Cmds;
import com.iot.common.constant.RespCode;
import com.iot.common.model.BaseMsg;
import com.iot.common.util.JsonUtil;

/**
 * Created by zc on 16-8-10.
 */
//模拟device的client
public class DeviceHandler extends AbstractHandler {


    public DeviceHandler(){
        super();
    }

    @Override
    public void onRead(ClientSocketChannel ctx, BaseMsg.BaseMsgPbOrBuilder msg)throws Exception {
        //System.out.println("======client recv::::" + msg.toString());
        switch (msg.getCmd()){
            case Cmds.CMD_PUSH_RSA_PUB_KEY:
                sendAesKey(ctx,msg);
                break;
            case Cmds.CMD_SEND_AES_KEY://resp
                onDiscussKeyResp(ctx,msg);
                break;
            case Cmds.CMD_DEVICE_AUTH://resp
                onDeviceAuthResp(msg);
                break;
            case Cmds.CMD_ANOTHOR_LOGIN:
                System.err.println("ctx is closed due to another login");
                ctx.disconnect();
                break;
            case Cmds.CMD_EXP:
                System.err.println("ctx is closed due to server internal exp");
                ctx.disconnect();
                break;
        }
    }

    private void onDiscussKeyResp(ClientSocketChannel ctx, BaseMsg.BaseMsgPbOrBuilder msg) {
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

    private void onDeviceAuthResp(BaseMsg.BaseMsgPbOrBuilder msg) {
        if(msg.getData().isEmpty()){
            System.err.println("=====>app auth resp msg is empty");
            return;
        }

        JSONObject json = JsonUtil.bytes2Json(msg.getData().toByteArray());
        int statusCode = json.getIntValue("code");

        if(statusCode== RespCode.COMMON_OK){
            System.out.println("=====>auth ok");
        }else{
            System.err.println("=====>onDiscussKeyResp::errCode::"+statusCode);
        }
    }

    private void doDeviceAuth(ClientSocketChannel ctx) {
        JSONObject json = new JSONObject();
        json.put("version","1.0");
        json.put("id",ClientEnv.CLIENT_ID);
        JSONArray jsonArray = new JSONArray();
        //add some abilities
        jsonArray.add("camera");
        jsonArray.add("fly");// -_-
        json.put("abilities",jsonArray);

        BaseMsg.BaseMsgPb.Builder builder = BaseMsg.BaseMsgPb.newBuilder();
        builder.setCmd(Cmds.CMD_DEVICE_AUTH);
        builder.setIsEncrypt(true);
        builder.setData(ByteString.copyFrom(JsonUtil.json2Bytes(json)));
        ctx.send(builder);
    }
}
