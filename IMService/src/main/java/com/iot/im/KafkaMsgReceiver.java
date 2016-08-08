package com.iot.im;

import com.iot.common.kafka.BaseKafkaConsumer;
import com.iot.common.kafka.KafkaMsg;

/**
 * Created by zc on 16-8-8.
 */
public class KafkaMsgReceiver implements BaseKafkaConsumer.KafkaProcessor {
    @Override
    public void process(String topic, Short key, KafkaMsg value) {

    }
}
