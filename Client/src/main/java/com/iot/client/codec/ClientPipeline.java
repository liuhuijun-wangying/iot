package com.iot.client.codec;

import com.iot.common.model.BaseMsg;
import com.iot.common.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public class ClientPipeline {

	private LengthFieldCodec stickPackCodec = new LengthFieldCodec();
	private BaseMsgCodec serializeCodec = new BaseMsgCodec();
	
	public List<BaseMsg.BaseMsgPb> decode(byte[] data) throws Exception {
		List<BaseMsg.BaseMsgPb> list = new ArrayList<>();
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
	
	public byte[] encode(BaseMsg.BaseMsgPbOrBuilder obj) throws Exception {
		if(obj==null){
			return null;
		}
		byte[] tmp = serializeCodec.encode(obj);
		return stickPackCodec.encode(tmp);
	}

}
