package com.iot.tcpserver;

import com.iot.common.util.TextUtil;
import com.iot.common.kafka.BaseKafkaConsumer;

/**
 * Created by zc on 16-8-8.
 */
public class ServiceRespHandler implements BaseKafkaConsumer.KafkaProcessor{

    @Override
    public void process(String topic, String key, byte[] value) {
        if(TextUtil.isEmpty(value)){
            return;
        }

    }
}
