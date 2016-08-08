package com.iot.common.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by zc on 16-8-8.
 */
public class BaseKafkaProducer {

    private KafkaProducer<String, byte[]> producer;
    private boolean hasInited = false;
    public void init(Properties prop) {
        if(hasInited){
            return;
        }
        prop.setProperty("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        prop.setProperty("value.serializer","org.apache.kafka.common.serialization.ByteArraySerializer");
        producer = new KafkaProducer<>(prop);
        //第一次发送耗时,先发送一个test
        send("test",null);
        hasInited = true;
    }

    private BaseKafkaProducer(){}
    private static BaseKafkaProducer instance;
    public static BaseKafkaProducer getInstance(){
        if(instance==null){
            synchronized (BaseKafkaProducer.class){
                if (instance==null){
                    instance = new BaseKafkaProducer();
                }
            }
        }
        return instance;
    }

    public void send(String topic, byte[] value){
        producer.send(new ProducerRecord<>(topic, value));
    }

    public void close(){
        if(producer!=null){
            producer.close();
        }
    }
}
