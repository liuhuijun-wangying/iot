package com.iot.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iot.client.codec.BaseMsg;
import com.iot.common.constant.Cmds;
import com.iot.common.constant.RespCode;
import com.iot.common.util.CryptUtil;
import com.iot.common.util.TextUtil;

import java.nio.charset.StandardCharsets;

/**
 * Created by zc on 16-8-10.
 */
//模拟device的client
public class DeviceHandler implements ChannelHandler<ClientSocketChannel,BaseMsg> {

    private static final BaseMsg HEARTBEAT_MSG = new BaseMsg(Cmds.CMD_HEARTBEAT,null);

    @Override
    public void onRead(ClientSocketChannel ctx, BaseMsg msg)throws Exception {
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
        }
    }

    @Override
    public void onIdle(ClientSocketChannel ctx){//send heartbeat pack
        ctx.send(HEARTBEAT_MSG);
    }

    @Override
    public void onConnected(ClientSocketChannel ctx){
        System.out.println("----onConnected----");
        //对于一些没有RSA计算能力的设备,可以不进行密钥协商,直接doAuth
        /*try {
            doDeviceAuth(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onClosed(){
        System.out.println("----onClosed----");
    }

    private void sendAesKey(ClientSocketChannel ctx, BaseMsg msg) throws Exception {
        byte[] b = CryptUtil.rsaEncryptByPublicKey(ClientEnv.AES_KEY,CryptUtil.bytes2PublicKey(msg.getData()));
        ctx.send(new BaseMsg(Cmds.CMD_SEND_AES_KEY,b));
    }

    private void onDiscussKeyResp(ClientSocketChannel ctx, BaseMsg msg) {
        if(TextUtil.isEmpty(msg.getData())){
            System.err.println("=====>discuss key resp msg is empty");
            return;
        }

        JSONObject json = JSON.parseObject(new String(msg.getData(),StandardCharsets.UTF_8));
        int statusCode = json.getIntValue("code");

        if(statusCode== RespCode.COMMON_OK){
            System.out.println("=====>discuss key ok");
            doDeviceAuth(ctx);
        }else{
            System.err.println("=====>discuss key failed, err code::"+statusCode);
        }
    }

    private void onDeviceAuthResp(BaseMsg msg) {
        if(TextUtil.isEmpty(msg.getData())){
            System.err.println("=====>app auth resp msg is empty");
            return;
        }

        JSONObject json = JSON.parseObject(new String(msg.getData(),StandardCharsets.UTF_8));
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
        ctx.send(new BaseMsg(Cmds.CMD_DEVICE_AUTH,false,json.toJSONString().getBytes(StandardCharsets.UTF_8)));
    }
}
