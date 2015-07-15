package com.hatiolab.dx.api;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SelectionKey;

import com.hatiolab.dx.mplexer.EventMultiplexer;
import com.hatiolab.dx.net.DiscoveryClient;
import com.hatiolab.dx.net.DiscoveryListener;
import com.hatiolab.dx.net.PacketClient;
import com.hatiolab.dx.net.PacketEventListener;

public class DxClient {
	
	protected EventMultiplexer mplexer;

	protected PacketClient packetClient;
	protected DiscoveryClient discoveryClient;
	
	protected SelectionKey key;
	
	public DxClient(EventMultiplexer mplexer, int discoveryServicePort, DiscoveryListener eventListener) throws Exception {
		this.mplexer = mplexer;

		discoveryClient = new DiscoveryClient(eventListener, discoveryServicePort, 0);
	}
	
	public void start() throws Exception {
		mplexer.register(discoveryClient.getSelectableChannel(), SelectionKey.OP_READ, discoveryClient.getSelectableHandler());
	}

	public void close() throws Exception {
		if (packetClient != null) {
			packetClient.close();
			packetClient = null;
		}
	}

	public boolean isConnected() {
		return packetClient != null && packetClient.isConnected();
	}
	
	public void discovery() throws Exception {
		discoveryClient.sendDiscoveryPacket();
	}
		
	public void startPacketClient(InetAddress address, int port, PacketEventListener eventListener) throws IOException {
		if(packetClient != null && packetClient.isConnected())
			return;
		
		packetClient = new PacketClient(eventListener, address.getHostAddress(), port);

		mplexer.register(packetClient.getSelectableChannel(), SelectionKey.OP_CONNECT, packetClient.getSelectableHandler());
	}
}
