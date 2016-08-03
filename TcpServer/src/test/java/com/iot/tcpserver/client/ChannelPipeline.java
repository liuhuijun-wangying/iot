package com.iot.tcpserver.client;

import com.iot.tcpserver.client.codec.LengthFieldCodec;
import com.iot.tcpserver.client.codec.BaseMsgCodec;
import com.iot.tcpserver.codec.BaseMsg;

import java.util.ArrayList;
import java.util.List;

public class ChannelPipeline {
	
	//socket到用户层
	List<BaseMsg> upstream(byte[] data){
		List<BaseMsg> list = new ArrayList<>();
		if(data==null || data.length==0){
			return list;
		}

		LengthFieldCodec.DecodeResult result = stickPackCodec.decode(data);
		if(result==null){
			return list;
		}

		list.add(serializeCodec.decode(result.data));
		if(result.needContinue){
			while(true){
				result = stickPackCodec.decode(null);
				list.add(serializeCodec.decode(result.data));
				if(!result.needContinue){
					break;
				}
			}
		}
		return list;
	}
	
	//用户层到socket
	byte[] downstream(BaseMsg obj){
		if(obj==null){
			return null;
		}

		byte[] tmp = serializeCodec.encode(obj);
		return stickPackCodec.encode(tmp);
	}

	private LengthFieldCodec stickPackCodec = new LengthFieldCodec();
	private BaseMsgCodec serializeCodec = new BaseMsgCodec();
	
}
