package com.iot.tcpserver.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.iot.common.constant.Cmds;
import com.iot.common.constant.RespCode;
import com.iot.common.model.BaseMsg;
import com.iot.common.util.CryptUtil;
import com.iot.common.util.JsonUtil;
import com.iot.common.util.TextUtil;
import com.iot.tcpserver.CtxPool;
import com.iot.tcpserver.ServerEnv;
import com.iot.tcpserver.client.Client;
import com.iot.tcpserver.client.DeviceClient;
import com.iot.tcpserver.util.Converter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created by zc on 16-9-1.
 */
//没有DB操作的，直接在这处理
public class NativeService {

    private static final Logger log = LoggerFactory.getLogger(NativeService.class);

    public static void doDeviceAuth(ChannelHandlerContext ctx, BaseMsg.BaseMsgPb baseMsg) {
        JSONObject deviceAuthJson = JsonUtil.bytes2Json(baseMsg.getData().toByteArray());
        if(deviceAuthJson==null){
            return;
        }

        JSONObject json;

        String id = deviceAuthJson.getString("id");
        if(TextUtil.isEmpty(id)){
            json = JsonUtil.buildCommonResp(RespCode.COMMON_EXCEPTION,"id is null");
        }else{
            ChannelHandlerContext oldCtx = CtxPool.getClient(id);
            if(oldCtx!=null && !oldCtx.channel().id().asLongText().equals(ctx.channel().id().asLongText())){
                oldCtx.writeAndFlush(BaseMsg.BaseMsgPb.newBuilder().setCmd(Cmds.CMD_ANOTHOR_LOGIN));
                CtxPool.removeClient(oldCtx);
                try {
                    oldCtx.close().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.warn("close old ctx due to new connect of device");
            }

            JSONArray abilites = deviceAuthJson.getJSONArray("abilities");
            Client client = new DeviceClient(id,deviceAuthJson.getString("version"), Arrays.asList(abilites.toArray(new String[]{})));
            ctx.channel().attr(ServerEnv.CLIENT).set(client);
            json = JsonUtil.buildCommonResp(RespCode.COMMON_OK,"ok");
            CtxPool.putClient(id,ctx);
        }

        BaseMsg.BaseMsgPb.Builder result = Converter.req2Resp(baseMsg);
        result.setData(ByteString.copyFrom(JsonUtil.json2Bytes(json)));
        ctx.writeAndFlush(result);
    }

    public static void doDiscussKey(ChannelHandlerContext ctx, BaseMsg.BaseMsgPb baseMsg) {
        JSONObject json;
        try{
            byte[] aesKey = CryptUtil.rsaDecryptByPrivate(baseMsg.getData().toByteArray(),ServerEnv.PRIVATE_KEY);
            if(!TextUtil.isEmpty(aesKey)){
                ctx.channel().attr(ServerEnv.KEY).set(aesKey);
                json = JsonUtil.buildCommonResp(RespCode.COMMON_OK,"ok");
            }else{
                json = JsonUtil.buildCommonResp(RespCode.COMMON_EXCEPTION,"aes key is null");
            }
        }catch (Exception e){
            json = JsonUtil.buildCommonResp(RespCode.COMMON_EXCEPTION,e.getMessage());
        }

        BaseMsg.BaseMsgPb.Builder result = Converter.req2Resp(baseMsg);
        result.setData(ByteString.copyFrom(JsonUtil.json2Bytes(json)));
        ctx.writeAndFlush(result);
    }

}
