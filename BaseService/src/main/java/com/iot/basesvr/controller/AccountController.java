package com.iot.basesvr.controller;

import com.alibaba.fastjson.JSONObject;
import com.iot.common.constant.Cmds;
import com.iot.common.util.JsonUtil;
import com.iot.basesvr.annotation.Cmd;
import com.iot.basesvr.service.AccountService;
import com.iot.common.util.TextUtil;
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
    public byte[] doAppRegist(byte[] param) throws Exception {
        if(TextUtil.isEmpty(param)){
            throw new NullPointerException("param is empty");
        }
        JSONObject data = JsonUtil.Bytes2Json(param);
        JSONObject appRegResult;
        if (data != null) {
            appRegResult = accountService.regist(data.getString("username"), data.getString("password"));
        } else {
            appRegResult = accountService.regist(null, null);
        }
        return JsonUtil.Json2Bytes(appRegResult);
    }

    @Cmd(value = Cmds.CMD_APP_AUTH)
    public byte[] doAppAuth(byte[] param) throws Exception {
        if(TextUtil.isEmpty(param)){
            throw new NullPointerException("param is empty");
        }
        JSONObject data = JsonUtil.Bytes2Json(param);
        JSONObject appAuthResult;
        if (data != null) {
            appAuthResult = accountService.login(data.getString("username"), data.getString("password"), data.getString("id"));
            appAuthResult.put("id",data.getString("id"));
            appAuthResult.put("version",data.getString("version"));
            appAuthResult.put("username",data.getString("username"));
        } else {
            appAuthResult = accountService.login(null, null, null);
        }
        return JsonUtil.Json2Bytes(appAuthResult);
    }
}
