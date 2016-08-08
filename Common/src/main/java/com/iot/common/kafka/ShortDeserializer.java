package com.iot.common.kafka;

import com.iot.common.util.NumUtil;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Created by zc on 16-8-8.
 */
public class ShortDeserializer implements Deserializer<Short> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do
    }

    @Override
    public Short deserialize(String topic, byte[] data) {
        if (data == null){
            return null;
        }

        if (data.length != 2) {
            throw new SerializationException("Size of data received by ShortDeserializer is " +
                    "not 2");
        }
        return NumUtil.bytes2Short(data);
    }

    @Override
    public void close() {
        // nothing to do
    }
}
