package com.iot.common.kafka;

import com.iot.common.util.NumUtil;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Created by zc on 16-8-8.
 */
public class IntegerSerializer implements Serializer<Integer> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do
    }

    @Override
    public byte[] serialize(String topic, Integer data) {
        if (data == null){
            return null;
        }
        return NumUtil.int2Bytes(data);
    }

    @Override
    public void close() {
        // nothing to do
    }
}
