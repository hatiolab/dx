package com.hatiolab.dx.api;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;

public interface EventListener {
	public void	onEvent(Header header, Data data) throws IOException;
	public void onConnected(SocketChannel channel);
	public void onDisconnected(SocketChannel channel);
}
