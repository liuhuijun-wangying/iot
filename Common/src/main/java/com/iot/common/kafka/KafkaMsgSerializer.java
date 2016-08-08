package com.iot.common.kafka;

import com.iot.common.util.NumUtil;
import com.iot.common.util.TextUtil;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.ByteBuffer;
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
        boolean isEmpty = TextUtil.isEmpty(data.getData());
        ByteBuffer buf = ByteBuffer.allocate(isEmpty?8:8+data.getData().length);
        buf.put(NumUtil.long2Bytes(data.getMsgId()));
        if(!isEmpty){
            buf.put(data.getData());
        }
        return buf.array();
    }

    @Override
    public void close() {
        // nothing to do
    }
}
