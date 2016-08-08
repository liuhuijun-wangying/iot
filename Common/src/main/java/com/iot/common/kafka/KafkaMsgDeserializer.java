package com.iot.common.kafka;

import com.iot.common.util.NumUtil;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Arrays;
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
        if (data == null){
            return null;
        }

        if (data.length < 8) {
            throw new SerializationException("Size of data received by KafkaMsgDeserializer is < 8");
        }

        KafkaMsg result = new KafkaMsg();
        result.setMsgId(NumUtil.bytes2Long(data));

        if(data.length>8){
            result.setData(Arrays.copyOfRange(data,8,data.length));
        }
        return result;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i=begin; i<begin+count; i++) bs[i-begin] = src[i];
        return bs;
    }

    @Override
    public void close() {
        // nothing to do
    }
}
