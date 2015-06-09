package com.hatiolab.dx.sample.client.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import com.hatiolab.dx.mplexer.EventMultiplexer;
import com.hatiolab.dx.net.DiscoveryClient;
import com.hatiolab.dx.net.PacketClient;
import com.hatiolab.dx.sample.server.Server;

public class Client {
	
	protected EventMultiplexer mplexer;

	protected PacketClient packetClient;
	protected DiscoveryClient discoveryClient;

	public static void main() {
		Client client = new Client();
		
		try {
			client.start(Server.DISCOVERY_SERVICE_PORT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start(int discoveryServicePort) throws Exception {
		mplexer = new EventMultiplexer();

		discoveryClient = new DiscoveryClient(new DiscoveryClientListener(this), Server.DISCOVERY_SERVICE_PORT, 0);
		
		SelectableChannel channel = discoveryClient.getSelectableChannel();
		SelectionKey key = channel.register(mplexer.getSelector(), SelectionKey.OP_READ);
		key.attach(discoveryClient.getSelectableHandler());	
		
		while(true) {
			mplexer.poll(1000);

			if(packetClient == null || packetClient.isConnected() == false) {
				discoveryClient.sendDiscoveryPacket();
			}
		}
	}

	public void stop() throws Exception {
		mplexer.close();
		discoveryClient.close();
	}
	
	public void startPacketClient(InetAddress address, int port) {
		if(packetClient != null && packetClient.isConnected())
			return;
		
		try {
			packetClient = new PacketClient(new PacketClientEventListener(), address.getHostAddress(), port);

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
