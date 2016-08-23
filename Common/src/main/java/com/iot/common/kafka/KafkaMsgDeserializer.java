package com.iot.common.kafka;

import com.iot.common.util.TextUtil;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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

        KafkaMsg result = new KafkaMsg();
        ByteBuffer buf = ByteBuffer.wrap(data);
        int channelIdLen = buf.getInt();
        result.setChannelId(new String(data,4,channelIdLen, StandardCharsets.UTF_8));
        buf.position(4+channelIdLen);
        result.setMsgId(buf.getLong());
        byte[] dest = new byte[buf.remaining()];
        buf.get(dest);
        result.setData(dest);
        return result;
    }

    @Override
    public void close() {
        // nothing to do
    }
}
