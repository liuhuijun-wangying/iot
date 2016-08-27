package com.iot.common.kafka;

import com.iot.common.model.KafkaMsg;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by zc on 16-8-8.
 */
public class BaseKafkaProducer {

    private KafkaProducer<Integer, KafkaMsg.KafkaMsgPbOrBuilder> producer;
    private boolean hasInited = false;
    public void init(Properties prop) {
        if(hasInited){
            return;
        }
        prop.setProperty("key.serializer","com.iot.common.kafka.IntegerSerializer");
        prop.setProperty("value.serializer","com.iot.common.kafka.KafkaMsgSerializer");
        producer = new KafkaProducer<>(prop);
        //第一次发送耗时,先发送一个test
        send("test", (short)0, null);
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

    public void send(String topic, int key, KafkaMsg.KafkaMsgPbOrBuilder value){
        producer.send(new ProducerRecord<>(topic, key, value));
    }

    public void close(){
        if(producer!=null){
            producer.close();
        }
    }
}
