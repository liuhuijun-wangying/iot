package com.iot.cs;

import com.iot.common.constant.Topics;
import com.iot.common.kafka.BaseKafkaConsumer;
import com.iot.common.kafka.BaseKafkaProducer;
import com.iot.common.model.KafkaMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by zc on 16-8-8.
 */
@Component
public class KafkaMsgReceiver implements BaseKafkaConsumer.KafkaProcessor{

    private Logger logger = LoggerFactory.getLogger(KafkaMsgReceiver.class);

    @Override
    public void process(String topic, Integer cmd, KafkaMsg.KafkaMsgPb value) {
        if(cmd == null || value==null){
            return;
        }

        ControllerScanner.CtrlMethod m = ControllerScanner.getMethod(cmd);
        if(m==null){
            return;
        }

        try {
            Object obj = m.invoke(value);
            if(obj==null){
                logger.warn("invoke return null");
                return;
            }
            if (obj instanceof KafkaMsg.KafkaMsgPbOrBuilder){
                BaseKafkaProducer.getInstance().send(Topics.TOPIC_SERVICE_RESP, cmd, (KafkaMsg.KafkaMsgPbOrBuilder)obj);
            }else{
                logger.warn("invoke return type is "+obj.getClass().getName()+" instead of KafkaMsg");
            }
        } catch (Exception e) {
            logger.error("invoke err: methos:"+m.obj+"--"+m.m.getName(),e);
        }
    }

}
