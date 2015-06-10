package com.hatiolab.dx.api;

import java.net.InetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hatiolab.dx.api.DxClient;
import com.hatiolab.dx.net.DiscoveryListener;
import com.hatiolab.dx.net.PacketEventListener;
import com.hatiolab.dx.sample.client.Client.PacketClientListener;
import com.hatiolab.dx.sample.server.Server;

public class DxClientTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
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
