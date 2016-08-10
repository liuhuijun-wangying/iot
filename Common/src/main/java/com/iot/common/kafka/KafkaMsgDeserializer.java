package com.iot.common.kafka;

import com.alibaba.fastjson.JSON;
import com.iot.common.util.TextUtil;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Created by zc on 16-8-8.
 */
public class KafkaMsgDeserializer implements Deserializer<KafkaMsg> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do
    }

    @Override
    public KafkaMsg deserialize(String topic, byte[] data) {
        if (TextUtil.isEmpty(data)){
            return null;
        }

        try {
            return JSON.parseObject(new String(data,"UTF-8"),KafkaMsg.class);
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public void close() {
        // nothing to do
    }
}
