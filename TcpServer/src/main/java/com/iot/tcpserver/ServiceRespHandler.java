package com.iot.tcpserver;

import com.iot.common.kafka.KafkaMsg;
import com.iot.common.kafka.BaseKafkaConsumer;

/**
 * Created by zc on 16-8-8.
 */
public class ServiceRespHandler implements BaseKafkaConsumer.KafkaProcessor{

    @Override
    public void process(String topic, Short key, KafkaMsg value) {


    }
}
