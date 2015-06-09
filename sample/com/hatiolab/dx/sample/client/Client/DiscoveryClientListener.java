package com.hatiolab.dx.sample.client.Client;

import java.net.InetAddress;

import com.hatiolab.dx.net.DiscoveryListener;

public class DiscoveryClientListener implements DiscoveryListener {
	
	protected Client client;
	
	public DiscoveryClientListener(Client client) {
		this.client = client;
	}

	@Override
	public void onFoundServer(InetAddress address, int port) {
		client.startPacketClient(address, port);
	}

}
