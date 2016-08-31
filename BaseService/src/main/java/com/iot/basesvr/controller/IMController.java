package com.iot.basesvr.controller;

import com.alibaba.fastjson.JSONObject;
import com.iot.basesvr.annotation.Cmd;
import com.iot.basesvr.service.IMService;
import com.iot.common.constant.Cmds;
import com.iot.common.model.KafkaMsg;
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
    public byte[] doAddDevice(KafkaMsg.KafkaMsgPb param) throws Exception {
        JSONObject data = JsonUtil.bytes2Json(param.getData().toByteArray());
        JSONObject result;
        if (data != null) {
            result = imService.addDevice(param.getClientId(),data.getString("deviceId"));
        } else {
            result = imService.addDevice(param.getClientId(),null);
        }
        return JsonUtil.json2Bytes(result);
    }

    @Cmd(value = Cmds.CMD_DEL_DEVICE)
    public byte[] doDelDevice(KafkaMsg.KafkaMsgPb param) throws Exception {
        JSONObject data = JsonUtil.bytes2Json(param.getData().toByteArray());
        JSONObject result;
        if (data != null) {
            result = imService.delDevice(param.getClientId(),data.getString("deviceId"));
        } else {
            result = imService.delDevice(param.getClientId(),null);
        }
        return JsonUtil.json2Bytes(result);
    }
}
