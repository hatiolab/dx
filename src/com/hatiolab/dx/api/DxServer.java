package com.hatiolab.dx.api;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import com.hatiolab.dx.mplexer.EventMultiplexer;
import com.hatiolab.dx.net.DiscoveryServer;
import com.hatiolab.dx.net.PacketEventListener;
import com.hatiolab.dx.net.PacketServer;

public class DxServer {

	public static final int DISCOVERY_SERVICE_PORT = 2015;
	public static final int PACKET_SERVICE_PORT = 2015;
	
	EventMultiplexer mplexer;

	PacketServer packetServer;
	DiscoveryServer discoveryServer;
	
	public DxServer() {}

	public void start(int packetServicePort, int discoveryServicePort, PacketEventListener eventListener) throws IOException {
		mplexer = new EventMultiplexer();
		discoveryServer = new DiscoveryServer(discoveryServicePort);
		packetServer = new PacketServer(eventListener, packetServicePort);

		SelectableChannel channel = discoveryServer.getSelectableChannel();
		SelectionKey key = channel.register(mplexer.getSelector(), SelectionKey.OP_READ);
		key.attach(discoveryServer.getSelectableHandler());
		
		channel = packetServer.getSelectableChannel();
		key = channel.register(mplexer.getSelector(), SelectionKey.OP_ACCEPT);
		key.attach(packetServer.getSelectableHandler());
		
		while(true) {
			mplexer.poll(1000);
		}
	}
	
	public void close() throws Exception {
		mplexer.close();
		packetServer.close();
		discoveryServer.close();
	}
}