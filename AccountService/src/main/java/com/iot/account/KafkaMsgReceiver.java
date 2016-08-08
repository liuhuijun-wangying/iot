package com.iot.account;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iot.common.constant.Cmds;
import com.iot.common.kafka.BaseKafkaConsumer;
import com.iot.common.kafka.KafkaMsg;
import com.iot.common.util.TextUtil;

/**
 * Created by zc on 16-8-8.
 */
public class KafkaMsgReceiver implements BaseKafkaConsumer.KafkaProcessor{
    @Override
    public void process(String topic, Short cmd, KafkaMsg value) {
        if(cmd == null || value==null){
            return;
        }

        JSONObject data = null;
        if(!TextUtil.isEmpty(value.getData())){
            try {
                data = JSON.parseObject(new String(value.getData(),"UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        handleMsg(cmd,value.getMsgId(),data);
    }

    private void handleMsg(short cmd, long msgId, JSONObject data){
        switch (cmd){
            case Cmds.CMD_APP_AUTH:
                if(data!=null){
                    System.err.println("=====>username::"+data.getString("username"));
                    System.err.println("=====>password::"+data.getString("password"));
                    System.err.println("=====>id::"+data.getString("id"));
                    System.err.println("=====>version::"+data.getString("version"));
                }
                break;
            case Cmds.CMD_DEVICE_AUTH:

                break;
        }
    }

}
