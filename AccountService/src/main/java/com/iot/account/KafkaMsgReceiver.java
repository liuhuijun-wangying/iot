package com.iot.account;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iot.account.service.AccountService;
import com.iot.account.service.impl.AccountServiceImpl;
import com.iot.common.constant.Cmds;
import com.iot.common.constant.Topics;
import com.iot.common.kafka.BaseKafkaConsumer;
import com.iot.common.kafka.BaseKafkaProducer;
import com.iot.common.kafka.KafkaMsg;
import com.iot.common.util.TextUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by zc on 16-8-8.
 */
public class KafkaMsgReceiver implements BaseKafkaConsumer.KafkaProcessor{

    private AccountService accountService = new AccountServiceImpl();

    @Override
    public void process(String topic, Short cmd, KafkaMsg value) {
        if(cmd == null || value==null){
            return;
        }

        if(!TextUtil.isEmpty(value.getData())){
            try {
                JSONObject data = JSON.parseObject(new String(value.getData(),"UTF-8"));
                handleMsg(cmd,value.getMsgId(),data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void handleMsg(short cmd, long msgId, JSONObject data) throws UnsupportedEncodingException {
        switch (cmd){
            case Cmds.CMD_APP_AUTH: {
                JSONObject appAuthResult;
                if (data != null) {
                    appAuthResult = accountService.login(data.getString("username"), data.getString("password"));
                } else {
                    appAuthResult = accountService.login(null, null);
                }
                KafkaMsg msg = new KafkaMsg(msgId, appAuthResult.toJSONString().getBytes("UTF-8"));
                BaseKafkaProducer.getInstance().send(Topics.TOPIC_SERVICE_RESP, Cmds.CMD_APP_AUTH, msg);
            }
                break;
            case Cmds.CMD_DEVICE_AUTH:

                break;
            case Cmds.CMD_APP_REGISTER:
                break;
        }
    }

}
