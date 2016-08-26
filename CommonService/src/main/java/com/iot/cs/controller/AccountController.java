package com.iot.cs.controller;

import com.alibaba.fastjson.JSONObject;
import com.iot.common.constant.Cmds;
import com.iot.common.kafka.KafkaMsg;
import com.iot.common.util.TextUtil;
import com.iot.cs.annotation.Cmd;
import com.iot.cs.service.AccountService;
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
    public KafkaMsg doAppRegist(KafkaMsg value){
        if(TextUtil.isEmpty(value.getData())){
            return null;
        }
        JSONObject data = value.getJsonData();
        JSONObject appRegResult;
        if (data != null) {
            appRegResult = accountService.regist(data.getString("username"), data.getString("password"));
        } else {
            appRegResult = accountService.regist(null, null);
        }
        return new KafkaMsg(value.getChannelId(),value.getMsgId()).setJsonData(appRegResult);
    }

    @Cmd(value = Cmds.CMD_APP_AUTH)
    public KafkaMsg doAppAuth(KafkaMsg value){
        if(TextUtil.isEmpty(value.getData())){
            return null;
        }
        JSONObject data = value.getJsonData();
        JSONObject appAuthResult;
        if (data != null) {
            appAuthResult = accountService.login(data.getString("username"), data.getString("password"), data.getString("id"));
            appAuthResult.put("id",data.getString("id"));
            appAuthResult.put("version",data.getString("version"));
            appAuthResult.put("username",data.getString("username"));
        } else {
            appAuthResult = accountService.login(null, null, null);
        }
        return new KafkaMsg(value.getChannelId(),value.getMsgId()).setJsonData(appAuthResult);
    }
}
