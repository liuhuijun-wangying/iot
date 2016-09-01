package com.iot.client;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.iot.common.constant.Cmds;
import com.iot.common.constant.RespCode;
import com.iot.common.model.BaseMsg;
import com.iot.common.util.CryptUtil;
import com.iot.common.util.JsonUtil;
import com.iot.common.util.TextUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zc on 16-8-10.
 */
//模拟app的client
public class AppHandler extends AbstractHandler {

    private boolean isLogined = false;

    public AppHandler(){
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
            case Cmds.CMD_APP_REGISTER://resp
                onRegResp(ctx,msg);
                break;
            case Cmds.CMD_APP_AUTH://resp
                onAppAuthResp(ctx,msg);
                break;
            case Cmds.CMD_ANOTHOR_LOGIN:
                System.err.println("ctx is closed due to another login");
                ctx.disconnect();
                break;
            case Cmds.CMD_EXP:
                System.err.println("ctx is closed due to server internal exp");
                ctx.disconnect();
                break;
            case Cmds.CMD_ADD_DEVICE:
                onAddDeviceResp(ctx,msg);
                break;
            case Cmds.CMD_IM:
                omImResp(msg);
                break;
        }
    }

    private Map<String,BaseMsg.BaseMsgPb.Builder> imMap = new HashMap<>();
    private void sendImMsg(ClientSocketChannel ctx){
        JSONObject json = new JSONObject();
        json.put("to",ClientEnv.CLIENT_ID);
        String msgId = TextUtil.uuid();
        json.put("msg","hello, im msg="+msgId);

        BaseMsg.BaseMsgPb.Builder builder = BaseMsg.BaseMsgPb.newBuilder();
        builder.setCmd(Cmds.CMD_IM);
        builder.setIsEncrypt(true);
        builder.setMsgId(msgId);
        builder.setData(ByteString.copyFrom(JsonUtil.json2Bytes(json)));
        //TODO deal with timeout msg
        imMap.put(msgId,builder);
        System.out.println("=====>send im msg, msgid="+msgId+",map size="+imMap.size());
        ctx.send(builder);
    }

    private void omImResp(BaseMsg.BaseMsgPbOrBuilder msg){
        System.out.println("=====>recv im resp::msgid="+msg.getMsgId()
            +",map contains="+imMap.containsKey(msg.getMsgId()));
        imMap.remove(msg.getMsgId());
    }

    @Override
    public void onIdle(ClientSocketChannel ctx) {//send heartbeat pack
        if (isLogined){//for test
            sendImMsg(ctx);
        }
    }

    /**************************************some base msg***********************************/

    private void onDiscussKeyResp(ClientSocketChannel ctx, BaseMsg.BaseMsgPbOrBuilder msg){
        if(msg.getData().isEmpty()){
            System.err.println("=====>discuss key resp msg is empty");
            return;
        }

        JSONObject json = JsonUtil.bytes2Json(msg.getData().toByteArray());
        int statusCode = json.getIntValue("code");

        if(statusCode== RespCode.COMMON_OK){
            //为了测试 先注册个账号
            System.out.println("=====>discuss key ok");
            doAppReg(ctx);
        }else{
            System.err.println("=====>discuss key failed, err code:"+statusCode);
        }
    }

    private void onRegResp(ClientSocketChannel ctx, BaseMsg.BaseMsgPbOrBuilder msg) {
        if(msg.getData().isEmpty()){
            System.err.println("=====>reg resp msg is empty");
            return;
        }

        JSONObject json = JsonUtil.bytes2Json(msg.getData().toByteArray());
        int statusCode = json.getIntValue("code");

        //测试环境下，为了方便，用户已存在也处理为注册成功
        //生产环境下，用户已存在要提示用户
        if(statusCode==RespCode.COMMON_OK || statusCode==RespCode.REG_USER_EXISTS){
            //reg成功后进行认证
            System.out.println("=====>reg ok");
            doAppAuth(ctx);
        }else{
            System.err.println("=====>on reg resp::errCode::"+statusCode);
        }
    }

    private void onAppAuthResp(ClientSocketChannel ctx, BaseMsg.BaseMsgPbOrBuilder msg){
        if(msg.getData().isEmpty()){
            System.err.println("=====>app auth resp msg is empty");
            return;
        }

        JSONObject json = JsonUtil.bytes2Json(msg.getData().toByteArray());
        int statusCode = json.getIntValue("code");

        if(statusCode==RespCode.COMMON_OK){
            System.out.println("=====>login ok");
            isLogined = true;
            doAddDevice(ctx);
        }else{
            System.err.println("=====>onDiscussKeyResp::errCode::"+statusCode);
        }
    }

    private void onAddDeviceResp(ClientSocketChannel ctx, BaseMsg.BaseMsgPbOrBuilder msg){
        if(msg.getData().isEmpty()){
            System.err.println("=====>onAddDeviceResp resp msg is empty");
            return;
        }

        JSONObject json = JsonUtil.bytes2Json(msg.getData().toByteArray());
        int statusCode = json.getIntValue("code");

        if(statusCode==RespCode.COMMON_OK || statusCode==RespCode.ADD_FRIEND_ALREADY){
            System.out.println("=====>add device ok");
        }else{
            System.err.println("=====>onAddDeviceResp::errCode::"+statusCode+"\nerrMsg:"+json.getString("msg"));
        }
    }

    private void doAppReg(ClientSocketChannel ctx) {
        JSONObject json = new JSONObject();
        json.put("username","zc_usr");
        json.put("password",CryptUtil.md5("zc_psw"));

        BaseMsg.BaseMsgPb.Builder builder = BaseMsg.BaseMsgPb.newBuilder();
        builder.setCmd(Cmds.CMD_APP_REGISTER);
        builder.setIsEncrypt(true);
        builder.setData(ByteString.copyFrom(JsonUtil.json2Bytes(json)));
        ctx.send(builder);
    }

    private void doAppAuth(ClientSocketChannel ctx){
        JSONObject json = new JSONObject();
        json.put("version","1.0");
        json.put("username","zc_usr");
        json.put("password",CryptUtil.md5("zc_psw"));

        BaseMsg.BaseMsgPb.Builder builder = BaseMsg.BaseMsgPb.newBuilder();
        builder.setCmd(Cmds.CMD_APP_AUTH);
        builder.setIsEncrypt(true);
        builder.setData(ByteString.copyFrom(JsonUtil.json2Bytes(json)));
        ctx.send(builder);
    }

    private void doAddDevice(ClientSocketChannel ctx){
        JSONObject json = new JSONObject();
        json.put("deviceId",ClientEnv.CLIENT_ID);

        BaseMsg.BaseMsgPb.Builder builder = BaseMsg.BaseMsgPb.newBuilder();
        builder.setCmd(Cmds.CMD_ADD_DEVICE);
        builder.setIsEncrypt(true);
        builder.setData(ByteString.copyFrom(JsonUtil.json2Bytes(json)));
        ctx.send(builder);
    }
}
