package com.iot.cs;

import com.alibaba.fastjson.JSONObject;
import com.iot.common.constant.Cmds;
import com.iot.common.constant.Topics;
import com.iot.common.kafka.BaseKafkaConsumer;
import com.iot.common.kafka.BaseKafkaProducer;
import com.iot.common.kafka.KafkaMsg;
import com.iot.common.util.TextUtil;
import com.iot.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by zc on 16-8-8.
 */
@Component
public class KafkaMsgReceiver implements BaseKafkaConsumer.KafkaProcessor{

    private Logger logger = LoggerFactory.getLogger(KafkaMsgReceiver.class);

    @Resource
    private AccountService accountService;

    @Override
    public void process(String topic, Short cmd, KafkaMsg value) {
        if(cmd == null || value==null){
            return;
        }

        switch (cmd){
            case Cmds.CMD_APP_AUTH:
                doAppAuth(value);
                break;
            case Cmds.CMD_APP_REGISTER:
                doAppRegist(value);
                break;
        }
    }

    private void doAppRegist(KafkaMsg value){
        if(TextUtil.isEmpty(value.getData())){
            return;
        }
        try {
            JSONObject data = value.getJsonData();
            JSONObject appRegResult;
            if (data != null) {
                appRegResult = accountService.regist(data.getString("username"), data.getString("password"));
            } else {
                appRegResult = accountService.regist(null, null);
            }
            KafkaMsg msg = new KafkaMsg(value.getChannelId(),value.getMsgId()).setJsonData(appRegResult);
            BaseKafkaProducer.getInstance().send(Topics.TOPIC_SERVICE_RESP, Cmds.CMD_APP_REGISTER, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doAppAuth(KafkaMsg value){
        if(TextUtil.isEmpty(value.getData())){
            return;
        }
        try {
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
            KafkaMsg msg = new KafkaMsg(value.getChannelId(),value.getMsgId()).setJsonData(appAuthResult);
            BaseKafkaProducer.getInstance().send(Topics.TOPIC_SERVICE_RESP, Cmds.CMD_APP_AUTH, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
