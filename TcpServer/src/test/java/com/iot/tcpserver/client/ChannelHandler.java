package com.iot.tcpserver.client;

public interface ChannelHandler<T,M> {
	
	void onConnected(T ctx);
	void onClosed();
	void onRead(T ctx, M msg);
	void onIdle(T ctx);
}
