package com.iot.common.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.iot.common.model.KafkaMsg;
import com.iot.common.util.TextUtil;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Created by zc on 16-8-8.
 */
public class KafkaMsgDeserializer implements Deserializer<KafkaMsg.KafkaMsgPb> {

    private static final KafkaMsg.KafkaMsgPb prototype = KafkaMsg.KafkaMsgPb.getDefaultInstance();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do
    }

    @Override
    public KafkaMsg.KafkaMsgPb deserialize(String topic, byte[] data) {
        if (TextUtil.isEmpty(data)){
            return null;
        }

        try {
            return prototype.getParserForType().parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {
        // nothing to do
    }
}
