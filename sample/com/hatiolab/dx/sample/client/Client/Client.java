package com.hatiolab.dx.sample.client.Client;

import java.io.IOException;
import java.net.InetAddress;

import com.hatiolab.dx.api.DxClient;
import com.hatiolab.dx.mplexer.EventMultiplexer;
import com.hatiolab.dx.net.DiscoveryListener;
import com.hatiolab.dx.net.PacketEventListener;
import com.hatiolab.dx.sample.server.Server;

public class Client {

	static DxClient client;
	
	static PacketEventListener packetEventListener = new PacketClientListener();
	
	static DiscoveryListener discoveryListener = new DiscoveryListener() {

		@Override
		public void onFoundServer(InetAddress address, int port) {
			try {
				client.startPacketClient(address, port, packetEventListener);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	public static void main() {
		
		try {
			EventMultiplexer mplexer = EventMultiplexer.getInstance();
			
			client = new DxClient(mplexer, Server.DISCOVERY_SERVICE_PORT, discoveryListener);
			client.start();
			
			while(true) {
				mplexer.poll(1000);
				
				if(!client.isConnected())
					client.discovery();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
