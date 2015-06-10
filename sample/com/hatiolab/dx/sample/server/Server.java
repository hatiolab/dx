package com.hatiolab.dx.sample.server;

import java.io.IOException;

import com.hatiolab.dx.api.DxServer;

public class Server {

	public static final int DISCOVERY_SERVICE_PORT = 2015;
	public static final int PACKET_SERVICE_PORT = 2015;
	
	public static void main() {
		DxServer server = new DxServer();
		
		try {
			server.start(PACKET_SERVICE_PORT, DISCOVERY_SERVICE_PORT, new PacketServerListener());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}