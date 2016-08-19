package com.iot.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iot.client.codec.BaseMsg;
import com.iot.common.constant.Cmds;
import com.iot.common.constant.RespCode;
import com.iot.common.util.CryptUtil;
import com.iot.common.util.TextUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by zc on 16-8-10.
 */
//模拟app的client
public class AppHandler implements ChannelHandler<ClientSocketChannel,BaseMsg> {

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
            case Cmds.CMD_APP_REGISTER://resp
                onRegResp(ctx,msg);
                break;
            case Cmds.CMD_APP_AUTH://resp
                onAppAuthResp(ctx,msg);
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
        byte[] b = CryptUtil.rsaEncryptByPublicKey(ClientEnv.AES_KEY,new String(msg.getData(),"UTF-8"));
        ctx.send(new BaseMsg(Cmds.CMD_SEND_AES_KEY,b));
    }

    private void onDiscussKeyResp(ClientSocketChannel ctx, BaseMsg msg) throws Exception {
        if(TextUtil.isEmpty(msg.getData())){
            System.err.println("=====>discuss key resp msg is empty");
            return;
        }

        JSONObject json = JSON.parseObject(new String(msg.getData(),"UTF-8"));
        int statusCode = json.getIntValue("code");

        if(statusCode== RespCode.COMMON_OK){
            //为了测试 先注册个账号
            System.out.println("=====>discuss key ok");
            doAppReg(ctx);
        }else{
            System.err.println("=====>discuss key failed, err code:"+statusCode);
        }
    }

    private void onRegResp(ClientSocketChannel ctx, BaseMsg msg) throws Exception {
        if(TextUtil.isEmpty(msg.getData())){
            System.err.println("=====>reg resp msg is empty");
            return;
        }

        JSONObject json = JSON.parseObject(new String(msg.getData(),"UTF-8"));
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

    private void onAppAuthResp(ClientSocketChannel ctx, BaseMsg msg) throws UnsupportedEncodingException {
        if(TextUtil.isEmpty(msg.getData())){
            System.err.println("=====>app auth resp msg is empty");
            return;
        }

        JSONObject json = JSON.parseObject(new String(msg.getData(),"UTF-8"));
        int statusCode = json.getIntValue("code");

        if(statusCode==RespCode.COMMON_OK){
            System.out.println("=====>login ok");
        }else{
            System.err.println("=====>onDiscussKeyResp::errCode::"+statusCode);
        }
    }

    private void doAppReg(ClientSocketChannel ctx) throws Exception{
        JSONObject json = new JSONObject();
        json.put("username","zc_usr");
        json.put("password",CryptUtil.md5("zc_psw"));
        ctx.send(new BaseMsg(Cmds.CMD_APP_REGISTER,true,json.toJSONString().getBytes("UTF-8")));
    }

    private void doAppAuth(ClientSocketChannel ctx) throws Exception {
        JSONObject json = new JSONObject();
        json.put("version","1.0");
        json.put("id","app123456789");
        json.put("username","zc_usr");
        json.put("password",CryptUtil.md5("zc_psw"));
        ctx.send(new BaseMsg(Cmds.CMD_APP_AUTH,true,json.toJSONString().getBytes("UTF-8")));
    }
}
