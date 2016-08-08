package com.iot.im;

import com.iot.common.constant.Topics;
import com.iot.common.kafka.BaseKafkaConsumer;
import com.iot.common.kafka.BaseKafkaProducer;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by zc on 16-8-8.
 */
public class IMMain {

    public static void main(String[] args) {
        try {
            initKafka();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseKafkaProducer.getInstance().close();
            BaseKafkaConsumer.getInstance().close();
        }
    }

    private static void initKafka() throws Exception {
        Properties producerProp = new Properties();
        InputStream producerIn = BaseKafkaProducer.class.getClassLoader().getResourceAsStream("producer.properties");
        producerProp.load(producerIn);
        BaseKafkaProducer.getInstance().init(producerProp);

        Properties consumerProp = new Properties();
        InputStream consumerIn = BaseKafkaConsumer.class.getClassLoader().getResourceAsStream("consumer.properties");
        consumerProp.load(consumerIn);
        BaseKafkaConsumer.getInstance().init(consumerProp, new String[]{Topics.TOPIC_IM},new KafkaMsgReceiver());
        BaseKafkaConsumer.getInstance().run();
    }
}
