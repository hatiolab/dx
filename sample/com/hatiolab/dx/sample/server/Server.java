package com.hatiolab.dx.sample.server;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import com.hatiolab.dx.mplexer.EventMultiplexer;
import com.hatiolab.dx.net.DiscoveryServer;
import com.hatiolab.dx.net.PacketServer;

public class Server {

	public static final int DISCOVERY_SERVICE_PORT = 2015;
	public static final int PACKET_SERVICE_PORT = 2015;
	
	EventMultiplexer mplexer;

	PacketServer packetServer;
	DiscoveryServer discoveryServer;

	public static void main() {
		Server server = new Server();
		
		try {
			server.start(PACKET_SERVICE_PORT, DISCOVERY_SERVICE_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start(int packetServicePort, int discoveryServicePort) throws IOException {
		mplexer = new EventMultiplexer();
		discoveryServer = new DiscoveryServer(discoveryServicePort);
		packetServer = new PacketServer(new PacketServerEventListener(), packetServicePort);

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