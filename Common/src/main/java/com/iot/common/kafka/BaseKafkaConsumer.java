package com.iot.common.kafka;

import com.iot.common.model.KafkaMsg;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zc on 16-8-8.
 */
public class BaseKafkaConsumer implements Runnable{

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

    private KafkaConsumer<Integer, KafkaMsg.KafkaMsgPb> consumer;
    private boolean hasInited = false;
    private KafkaProcessor processor;
    private ExecutorService fixedThreadPool;
    public void init(Properties prop, String[] topics, KafkaProcessor processor) throws IOException {
        if(hasInited){
            return;
        }
        if(processor==null){
            throw new NullPointerException("processor = null makes no sense");
        }
        int threadNum = Integer.parseInt(prop.getProperty("process.num","0"));
        if(threadNum>0){
            fixedThreadPool = Executors.newFixedThreadPool(threadNum);
        }
        this.processor = processor;
        prop.setProperty("key.deserializer","com.iot.common.kafka.IntegerDeserializer");
        prop.setProperty("value.deserializer","com.iot.common.kafka.KafkaMsgDeserializer");
        consumer = new KafkaConsumer<>(prop);
        consumer.subscribe(Arrays.asList(topics));
        hasInited = true;
    }

    public interface KafkaProcessor{
        void process(String topic,Integer key,KafkaMsg.KafkaMsgPb value);
    }

    private boolean isRunning = false;
    @Override
    public void run(){
        isRunning = true;
        while (isRunning && !Thread.interrupted()) {
            ConsumerRecords<Integer, KafkaMsg.KafkaMsgPb> records = consumer.poll(100);
            if(records.isEmpty()){
                continue;
            }
            for (ConsumerRecord<Integer, KafkaMsg.KafkaMsgPb> record : records) {
                if(fixedThreadPool==null){
                    processor.process(record.topic(),record.key(),record.value());
                }else{
                    fixedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            processor.process(record.topic(),record.key(),record.value());
                        }
                    });
                }
            }
            consumer.commitAsync();
        }
        if(consumer!=null){
            consumer.close();
        }
    }

    public void close(){
        isRunning = false;
    }
}
