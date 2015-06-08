package com.hatiolab.dx.api;

import java.nio.channels.SocketChannel;

import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;

public interface EventListener {
	public void	onEvent(SocketChannel channel, Header header, Data data);
	public void onConnected(SocketChannel channel);
	public void onDisconnected(SocketChannel channel);
}
