package com.iot.tcpserver.client.codec;

import com.iot.tcpserver.codec.BaseMsg;
import com.iot.tcpserver.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public class ChannelPipeline {

	private LengthFieldCodec stickPackCodec = new LengthFieldCodec();
	private BaseMsgCodec serializeCodec = new BaseMsgCodec();
	
	public List<BaseMsg> decode(byte[] data) throws Exception {
		List<BaseMsg> list = new ArrayList<>();
		if(TextUtil.isEmpty(data)){
			return list;
		}

		LengthFieldCodec.DecodeResult result = stickPackCodec.decode(data);
		if(result==null){
			return list;
		}

		if(result.data!=null){
			list.add(serializeCodec.decode(result.data));
		}
		if(result.needContinue){
			while(true){
				result = stickPackCodec.decode(null);
				if(result.data!=null){
					list.add(serializeCodec.decode(result.data));
				}
				if(!result.needContinue){
					break;
				}
			}
		}
		return list;
	}
	
	public byte[] encode(BaseMsg obj) throws Exception {
		if(obj==null){
			return null;
		}
		byte[] tmp = serializeCodec.encode(obj);
		return stickPackCodec.encode(tmp);
	}

}
