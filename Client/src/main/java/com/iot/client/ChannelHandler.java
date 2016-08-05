package com.iot.client;

public interface ChannelHandler<T,M> {
	
	void onConnected(T ctx);
	void onClosed();
	void onRead(T ctx, M msg)throws Exception;
	void onIdle(T ctx);
}
