package com.iot.tcpserver.util;

import com.iot.common.model.BaseMsg;
import com.iot.common.model.KafkaMsg;

/**
 * Created by zc on 16-9-1.
 */
public class Converter {

    public static KafkaMsg.KafkaMsgPb.Builder baseMsg2KafkaMsg(BaseMsg.BaseMsgPb baseMsg){
        KafkaMsg.KafkaMsgPb.Builder kafkaMsg = KafkaMsg.KafkaMsgPb.newBuilder();
        kafkaMsg.setMsgId(baseMsg.getMsgId());
        kafkaMsg.setData(baseMsg.getData());
        kafkaMsg.setIsEncrypt(baseMsg.getIsEncrypt());
        return kafkaMsg;
    }

    public static BaseMsg.BaseMsgPb.Builder req2Resp(BaseMsg.BaseMsgPb baseMsg){
        BaseMsg.BaseMsgPb.Builder result = BaseMsg.BaseMsgPb.newBuilder();
        result.setCmd(baseMsg.getCmd());
        result.setMsgId(baseMsg.getMsgId());
        result.setIsEncrypt(baseMsg.getIsEncrypt());
        return result;
    }
}
