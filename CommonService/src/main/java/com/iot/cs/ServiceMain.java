package com.iot.cs;

import com.iot.common.constant.Topics;
import com.iot.common.kafka.BaseKafkaConsumer;
import com.iot.common.kafka.BaseKafkaProducer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by zc on 16-8-8.
 */
public class ServiceMain {

    private static ApplicationContext ctx;

    public static void main(String[] args) {
        try {
            initSpring();
            initKafka();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            BaseKafkaProducer.getInstance().close();
            BaseKafkaConsumer.getInstance().close();
        }
    }

    private static void initSpring() {
        ctx = new ClassPathXmlApplicationContext("classpath:spring.xml");
    }

    private static void initKafka() throws IOException {
        Properties producerProp = new Properties();
        InputStream producerIn = BaseKafkaProducer.class.getClassLoader().getResourceAsStream("producer.properties");
        producerProp.load(producerIn);
        BaseKafkaProducer.getInstance().init(producerProp);

        KafkaMsgReceiver ks = ctx.getBean("kafkaMsgReceiver",KafkaMsgReceiver.class);
        Properties consumerProp = new Properties();
        InputStream consumerIn = BaseKafkaConsumer.class.getClassLoader().getResourceAsStream("consumer.properties");
        consumerProp.load(consumerIn);
        BaseKafkaConsumer.getInstance().init(consumerProp, new String[]{Topics.TOPIC_SERVICE},ks);
        BaseKafkaConsumer.getInstance().run();
    }
}
