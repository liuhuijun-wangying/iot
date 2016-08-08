package com.iot.common.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by zc on 16-8-8.
 */
public class BaseKafkaConsumer extends Thread{

    private BaseKafkaConsumer(){}
    private static BaseKafkaConsumer instance;
    public static BaseKafkaConsumer getInstance(){
        if(instance==null){
            synchronized (BaseKafkaConsumer.class){
                if (instance==null){
                    instance = new BaseKafkaConsumer();
                }
            }
        }
        return instance;
    }

    private KafkaConsumer<String, byte[]> consumer;
    private boolean hasInited = false;
    private KafkaProcessor processor;
    public void init(Properties prop, String[] topics, KafkaProcessor processor) throws IOException {
        if(hasInited){
            return;
        }
        if(processor==null){
            throw new NullPointerException("processor = null makes no sense");
        }
        this.processor = processor;
        prop.setProperty("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        prop.setProperty("value.deserializer","org.apache.kafka.common.serialization.ByteArrayDeserializer");
        consumer = new KafkaConsumer<>(prop);
        consumer.subscribe(Arrays.asList(topics));
        hasInited = true;
    }

    public interface KafkaProcessor{
        void process(String topic,String key,byte[] value);
    }

    private boolean isRunning = false;
    @Override
    public void run(){
        isRunning = true;
        while (isRunning && !Thread.interrupted()) {
            for (ConsumerRecord<String, byte[]> record : consumer.poll(100)) {
                processor.process(record.topic(),record.key(),record.value());
            }
        }
        if(consumer!=null){
            consumer.close();
        }
    }

    public void close(){
        isRunning = false;
    }
}
