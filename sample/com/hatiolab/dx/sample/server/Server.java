package com.hatiolab.dx.sample.server;

import java.io.IOException;

import com.hatiolab.dx.api.DxServer;
import com.hatiolab.dx.mplexer.EventMultiplexer;

public class Server {

	public static final int DISCOVERY_SERVICE_PORT = 2015;
	public static final int PACKET_SERVICE_PORT = 2015;
	
	public static void main() {

		try {
			EventMultiplexer mplexer = new EventMultiplexer();

			DxServer server = new DxServer(mplexer, PACKET_SERVICE_PORT, DISCOVERY_SERVICE_PORT, new PacketServerListener());

			while(true) {
				mplexer.poll(1000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}