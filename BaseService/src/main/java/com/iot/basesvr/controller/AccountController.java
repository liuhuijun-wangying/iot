package com.iot.basesvr.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.iot.common.constant.Cmds;
import com.iot.common.model.KafkaMsg;
import com.iot.common.util.JsonUtil;
import com.iot.basesvr.annotation.Cmd;
import com.iot.basesvr.service.AccountService;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * Created by zc on 16-8-26.
 */
@Controller
public class AccountController {

    @Resource
    private AccountService accountService;

    @Cmd(value = Cmds.CMD_APP_REGISTER)
    public KafkaMsg.KafkaMsgPbOrBuilder doAppRegist(KafkaMsg.KafkaMsgPb value){
        if(value.getData().isEmpty()){
            return null;
        }
        JSONObject data = JsonUtil.Bytes2Json(value.getData().toByteArray());
        JSONObject appRegResult;
        if (data != null) {
            appRegResult = accountService.regist(data.getString("username"), data.getString("password"));
        } else {
            appRegResult = accountService.regist(null, null);
        }
        KafkaMsg.KafkaMsgPb.Builder builder = KafkaMsg.KafkaMsgPb.newBuilder();
        builder.setMsgId(value.getMsgId());
        builder.addAllChannelId(value.getChannelIdList());
        builder.setData(ByteString.copyFrom(JsonUtil.Json2Bytes(appRegResult)));
        return builder;
    }

    @Cmd(value = Cmds.CMD_APP_AUTH)
    public KafkaMsg.KafkaMsgPbOrBuilder doAppAuth(KafkaMsg.KafkaMsgPb value){
        if(value.getData().isEmpty()){
            return null;
        }
        JSONObject data = JsonUtil.Bytes2Json(value.getData().toByteArray());
        JSONObject appAuthResult;
        if (data != null) {
            appAuthResult = accountService.login(data.getString("username"), data.getString("password"), data.getString("id"));
            appAuthResult.put("id",data.getString("id"));
            appAuthResult.put("version",data.getString("version"));
            appAuthResult.put("username",data.getString("username"));
        } else {
            appAuthResult = accountService.login(null, null, null);
        }
        KafkaMsg.KafkaMsgPb.Builder builder = KafkaMsg.KafkaMsgPb.newBuilder();
        builder.setMsgId(value.getMsgId());
        builder.addAllChannelId(value.getChannelIdList());
        builder.setData(ByteString.copyFrom(JsonUtil.Json2Bytes(appAuthResult)));
        return builder;
    }
}
