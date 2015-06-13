package com.hatiolab.dx.api;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SelectableChannel;
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
	
	public DxClient(EventMultiplexer mplexer, int discoveryServicePort, DiscoveryListener eventListener) throws Exception {
		this.mplexer = mplexer;

		discoveryClient = new DiscoveryClient(eventListener, discoveryServicePort, 0);
		
		SelectableChannel channel = discoveryClient.getSelectableChannel();
		SelectionKey key = channel.register(mplexer.getSelector(), SelectionKey.OP_READ);
		key.attach(discoveryClient.getSelectableHandler());	
	}

	public void close() throws Exception {
		discoveryClient.close();
	}

	public boolean isConnected() {
		return packetClient != null && packetClient.isConnected();
	}
	
	public void discovery() throws Exception {
		discoveryClient.sendDiscoveryPacket();
	}
	
	public void startPacketClient(InetAddress address, int port, PacketEventListener eventListener) {
		if(packetClient != null)
			return;
		
		try {
			packetClient = new PacketClient(eventListener, address.getHostAddress(), port);

			SelectableChannel channel = packetClient.getSelectableChannel();
			SelectionKey key = channel.register(mplexer.getSelector(), SelectionKey.OP_CONNECT);
			key.attach(packetClient.getSelectableHandler());
			
		} catch (IOException e) {
			e.printStackTrace();
			try {
				packetClient.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			packetClient = null;
		}
	}
}
