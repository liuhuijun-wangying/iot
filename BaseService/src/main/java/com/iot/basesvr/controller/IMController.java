package com.iot.basesvr.controller;

import com.alibaba.fastjson.JSONObject;
import com.iot.basesvr.annotation.Cmd;
import com.iot.basesvr.service.IMService;
import com.iot.common.constant.Cmds;
import com.iot.common.util.JsonUtil;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * Created by zc on 16-8-26.
 */
@Controller
public class IMController {

    @Resource
    private IMService imService;

    @Cmd(value = Cmds.CMD_ADD_DEVICE)
    public byte[] doAddDevice(byte[] param) throws Exception {
        JSONObject data = JsonUtil.bytes2Json(param);
        JSONObject result;
        if (data != null) {
            result = imService.addDevice(data.getString("from"),data.getString("deviceId"));
        } else {
            result = imService.addDevice(null,null);
        }
        return JsonUtil.json2Bytes(result);
    }

}
