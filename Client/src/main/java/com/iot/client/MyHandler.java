package com.iot.client;

import com.alibaba.fastjson.JSONObject;
import com.iot.client.codec.BaseMsg;
import com.iot.common.constant.Cmds;
import com.iot.common.util.CryptUtil;
import com.iot.common.util.TextUtil;

/**
 * Created by zc on 16-8-10.
 */
public class MyHandler implements ChannelHandler<ClientSocketChannel,BaseMsg> {

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

        //1代表密钥协商成功
        //2 failed
        if(msg.getData()[0]==1){
            //为了测试 先注册个账号
            System.out.println("=====>discuss key ok");
            doAppReg(ctx);
        }else{
            System.err.println("=====>discuss key failed");
        }
    }

    private void onRegResp(ClientSocketChannel ctx, BaseMsg msg) throws Exception {
        if(TextUtil.isEmpty(msg.getData())){
            System.err.println("=====>reg resp msg is empty");
            return;
        }

        //1 注册ok
        //2 用户已经存在
        //3 发送过去的用户名和密码为empty(为了防止客户端没有做判断)
        //4 服务器端抛异常
        if(msg.getData()[0]==1 || msg.getData()[0]==2){
            //reg成功后进行认证
            System.out.println("=====>reg ok");
            doAppAuth(ctx);
        }else{
            System.err.println("=====>on reg resp::errCode::"+msg.getData()[0]);
        }
    }

    private void onAppAuthResp(ClientSocketChannel ctx, BaseMsg msg) {
        if(TextUtil.isEmpty(msg.getData())){
            System.err.println("=====>app auth resp msg is empty");
            return;
        }

        //1 login ok
        //2 用户名或者密码错误
        //3 发送过去的用户名和密码为empty(为了防止客户端没有做判断)
        //4 服务器端抛异常
        if(msg.getData()[0]==1){
            System.out.println("=====>login ok");
        }else{
            System.err.println("=====>onDiscussKeyResp::errCode::"+msg.getData()[0]);
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
    private void doDeviceAuth(ClientSocketChannel ctx) throws Exception {
        JSONObject json = new JSONObject();
        json.put("version","1.0");
        json.put("id","device1234567890");
        ctx.send(new BaseMsg(Cmds.CMD_APP_AUTH,false,json.toJSONString().getBytes("UTF-8")));
    }
}
