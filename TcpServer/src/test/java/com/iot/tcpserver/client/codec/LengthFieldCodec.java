package com.iot.tcpserver.client.codec;

import com.iot.tcpserver.util.NumUtil;
import com.iot.tcpserver.util.TextUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 粘包半包处理，仅支持如下格式
 * |--length(4byte,不包含自己的长度)--| |--data--|
 */
public class LengthFieldCodec{
	
	private byte[] cache;
	
	//用户层到socket
	public byte[] encode(byte[] data){
		ByteBuffer buf = ByteBuffer.allocate(4+data.length);
		buf.put(NumUtil.int2Bytes(data.length));
		buf.put(data);
		return buf.array();
	}
	
	public class DecodeResult{
		public byte[] data;
		public boolean needContinue;
	}
	
	//socket到用户层
	public DecodeResult decode(byte[] data){
		if(TextUtil.isEmpty(data) && TextUtil.isEmpty(cache)){
			return null;
		}
		if(!TextUtil.isEmpty(data) && TextUtil.isEmpty(cache)){
			return doDecode(data);
		}
		if(TextUtil.isEmpty(data) && !TextUtil.isEmpty(cache)){
			byte[] tmp = cache.clone();
			cache = null;
			return doDecode(tmp);
		}
		
		ByteBuffer buf = ByteBuffer.allocate(cache.length+data.length);
		buf.put(cache);
		buf.put(data);
		cache = null;
		return doDecode(buf.array());
	}
	
	private DecodeResult doDecode(byte[] data){
		if(data.length<=4){
			cache = data;
		}else{
			int length = NumUtil.bytes2Int(data);
			if(length==0){
				return null;
			}
			if(data.length-4 == length){
				DecodeResult result = new DecodeResult();
				result.data = Arrays.copyOfRange(data, 4, data.length);
				result.needContinue = false;
				return result;
			}
			if(data.length-4 > length){
				cache = Arrays.copyOfRange(data, length+4, data.length);
				DecodeResult result = new DecodeResult();
				result.data = Arrays.copyOfRange(data, 4, length+4);
				result.needContinue = true;
				return result;
			}
			cache = data;
		}
		return null;
	}
	

}
