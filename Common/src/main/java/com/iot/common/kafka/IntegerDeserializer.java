package com.iot.common.kafka;

import com.iot.common.util.NumUtil;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Created by zc on 16-8-8.
 */
public class IntegerDeserializer implements Deserializer<Integer> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do
    }

    @Override
    public Integer deserialize(String topic, byte[] data) {
        if (data == null){
            return null;
        }

        if (data.length != 4) {
            throw new SerializationException("Size of data received by IntegerDeserializer is " +
                    "not 4");
        }
        return NumUtil.bytes2Int(data);
    }

    @Override
    public void close() {
        // nothing to do
    }
}
