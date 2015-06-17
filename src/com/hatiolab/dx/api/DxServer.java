package com.hatiolab.dx.api;

import java.io.IOException;
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
	
	SelectionKey key;
	
	public DxServer(EventMultiplexer mplexer, int packetServicePort, int discoveryServicePort, PacketEventListener eventListener) throws IOException {
		this.mplexer = mplexer;
		
		packetServer = new PacketServer(eventListener, packetServicePort);
		discoveryServer = new DiscoveryServer(discoveryServicePort, packetServer.getServicePort());
	}
	
	public void start() {
		if(packetServer != null)
			mplexer.register(packetServer.getSelectableChannel(), SelectionKey.OP_ACCEPT, packetServer.getSelectableHandler());
		
		if(discoveryServer != null)
			mplexer.register(discoveryServer.getSelectableChannel(), SelectionKey.OP_READ, discoveryServer.getSelectableHandler());
	}

	public void close() throws Exception {
		packetServer.close();
		discoveryServer.close();
	}
}