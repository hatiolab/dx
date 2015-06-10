package com.hatiolab.dx.sample.client.Client;

import java.net.InetAddress;

import com.hatiolab.dx.api.DxClient;
import com.hatiolab.dx.net.DiscoveryListener;
import com.hatiolab.dx.net.PacketEventListener;
import com.hatiolab.dx.sample.server.Server;

public class Client {

	public static void main() {
		final DxClient client = new DxClient();
		
		final PacketEventListener packetEventListener = new PacketClientListener();
		DiscoveryListener discoveryListener = new DiscoveryListener() {

			@Override
			public void onFoundServer(InetAddress address, int port) {
				client.startPacketClient(address, port, packetEventListener);
			}
		};
		
		try {
			client.start(Server.DISCOVERY_SERVICE_PORT, discoveryListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
