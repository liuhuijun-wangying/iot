package com.iot.common.kafka;

import com.iot.common.model.KafkaMsg;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Created by zc on 16-8-8.
 */
public class KafkaMsgSerializer implements Serializer<KafkaMsg.KafkaMsgPbOrBuilder> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do
    }

    @Override
    public byte[] serialize(String topic, KafkaMsg.KafkaMsgPbOrBuilder data) {
        if (data == null){
            return null;
        }

        if (data instanceof KafkaMsg.KafkaMsgPb){
            return ((KafkaMsg.KafkaMsgPb)data).toByteArray();
        }else if (data instanceof KafkaMsg.KafkaMsgPb.Builder){
            return ((KafkaMsg.KafkaMsgPb.Builder)data).build().toByteArray();
        }
        return null;
    }

    @Override
    public void close() {
        // nothing to do
    }
}
