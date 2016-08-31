package com.iot.basesvr.controller;

import com.alibaba.fastjson.JSONObject;
import com.iot.common.constant.Cmds;
import com.iot.common.constant.RespCode;
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
    public byte[] doAppRegist(KafkaMsg.KafkaMsgPb param) throws Exception {
        JSONObject data = JsonUtil.bytes2Json(param.getData().toByteArray());
        JSONObject appRegResult;
        if (data != null) {
            appRegResult = accountService.regist(data.getString("username"), data.getString("password"));
        } else {
            appRegResult = accountService.regist(null, null);
        }
        return JsonUtil.json2Bytes(appRegResult);
    }

    @Cmd(value = Cmds.CMD_APP_AUTH)
    public byte[] doAppAuth(KafkaMsg.KafkaMsgPb param) throws Exception {
        JSONObject data = JsonUtil.bytes2Json(param.getData().toByteArray());
        JSONObject appAuthResult;
        if (data != null) {
            appAuthResult = accountService.login(data.getString("username"), data.getString("password"));
            appAuthResult.put("version",data.getString("version"));
            appAuthResult.put("username",data.getString("username"));
        } else {
            appAuthResult = accountService.login(null, null);
        }

        //TODO offline msg
        if (appAuthResult.getIntValue("code")== RespCode.COMMON_OK){//login ok

        }
        return JsonUtil.json2Bytes(appAuthResult);
    }
}
