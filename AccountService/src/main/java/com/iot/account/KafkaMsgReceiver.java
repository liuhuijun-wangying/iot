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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zc on 16-8-8.
 */
public class KafkaMsgReceiver implements BaseKafkaConsumer.KafkaProcessor{

    private Logger log = LoggerFactory.getLogger(KafkaMsgReceiver.class);

    private AccountService accountService = new AccountServiceImpl();

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
            JSONObject data = JSON.parseObject(new String(value.getData(),"UTF-8"));
            JSONObject appRegResult;
            if (data != null) {
                appRegResult = accountService.regist(data.getString("username"), data.getString("password"));
            } else {
                appRegResult = accountService.regist(null, null);
            }
            KafkaMsg msg = new KafkaMsg(value.getMsgId(),value.getChannelId(),appRegResult.toJSONString().getBytes("UTF-8"));
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
            JSONObject data = JSON.parseObject(new String(value.getData(),"UTF-8"));
            JSONObject appAuthResult;
            if (data != null) {
                appAuthResult = accountService.login(data.getString("username"), data.getString("password"));
                appAuthResult.put("id",data.getString("id"));
                appAuthResult.put("version",data.getString("version"));
                appAuthResult.put("username",data.getString("username"));
            } else {
                appAuthResult = accountService.login(null, null);
            }
            KafkaMsg msg = new KafkaMsg(value.getMsgId(),value.getChannelId(),appAuthResult.toJSONString().getBytes("UTF-8"));
            //log.info("doAppAuth resp msg::"+msg.toString());
            BaseKafkaProducer.getInstance().send(Topics.TOPIC_SERVICE_RESP, Cmds.CMD_APP_AUTH, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
