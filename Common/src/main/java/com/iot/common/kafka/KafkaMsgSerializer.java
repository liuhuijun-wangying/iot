package com.iot.common.kafka;

import org.apache.kafka.common.serialization.Serializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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

        byte[] channelIdBytes = data.getChannelId().getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(4+channelIdBytes.length+8+data.getData().length);
        buf.putInt(channelIdBytes.length);
        buf.put(channelIdBytes);
        buf.putLong(data.getMsgId());
        buf.put(data.getData());
        return buf.array();
    }

    @Override
    public void close() {
        // nothing to do
    }
}
