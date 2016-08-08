package com.iot.account;

import com.iot.common.constant.Topics;
import com.iot.common.kafka.BaseKafkaConsumer;
import com.iot.common.kafka.BaseKafkaProducer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by zc on 16-8-8.
 */
public class AccountMain {

    public static void main(String[] args) {
        try {
            initKafka();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            BaseKafkaProducer.getInstance().close();
            BaseKafkaConsumer.getInstance().close();
        }
    }

    private static void initKafka() throws IOException {
        Properties producerProp = new Properties();
        InputStream producerIn = BaseKafkaProducer.class.getClassLoader().getResourceAsStream("producer.properties");
        producerProp.load(producerIn);
        BaseKafkaProducer.getInstance().init(producerProp);

        Properties consumerProp = new Properties();
        InputStream consumerIn = BaseKafkaConsumer.class.getClassLoader().getResourceAsStream("consumer.properties");
        consumerProp.load(consumerIn);
        BaseKafkaConsumer.getInstance().init(consumerProp, new String[]{Topics.TOPIC_ACCOUNT},new KafkaMsgReceiver());
        BaseKafkaConsumer.getInstance().run();
    }
}
