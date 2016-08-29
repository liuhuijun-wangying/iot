package com.iot.basesvr;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.iot.common.constant.RespCode;
import com.iot.common.constant.Topics;
import com.iot.common.kafka.BaseKafkaConsumer;
import com.iot.common.kafka.BaseKafkaProducer;
import com.iot.common.model.KafkaMsg;
import com.iot.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

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

        KafkaMsg.KafkaMsgPb.Builder builder = KafkaMsg.KafkaMsgPb.newBuilder();
        builder.setMsgId(value.getMsgId());
        builder.addAllChannelId(value.getChannelIdList());

        try {
            Object obj = m.invoke(value.getData().toByteArray());
            builder.setData(ByteString.copyFrom((byte[])obj));
        } catch (InvocationTargetException methodExp){
            JSONObject expJson = JsonUtil.buildCommonResp(RespCode.COMMON_EXCEPTION,methodExp.getMessage());
            builder.setData(ByteString.copyFrom(JsonUtil.Json2Bytes(expJson)));
        } catch (Exception e) {
            logger.error("invoke err: methos:"+m.obj+"--"+m.m.getName(),e);
            JSONObject expJson = JsonUtil.buildCommonResp(RespCode.COMMON_EXCEPTION,"server internal error");
            builder.setData(ByteString.copyFrom(JsonUtil.Json2Bytes(expJson)));
        }

        BaseKafkaProducer.getInstance().send(Topics.TOPIC_SERVICE_RESP, cmd, builder);
    }

}
