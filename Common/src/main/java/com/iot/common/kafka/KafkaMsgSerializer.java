package com.iot.common.kafka;

import org.apache.kafka.common.serialization.Serializer;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by zc on 16-8-8.
 */
public class KafkaMsgSerializer implements Serializer<KafkaMsg> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do
    }

    @Override
    public byte[] serialize(String topic, KafkaMsg data) {
        if (data == null){
            return null;
        }

        try {
            return data.toJsonString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException cannotHappen) {
            return null;
        }
    }

    @Override
    public void close() {
        // nothing to do
    }
}
