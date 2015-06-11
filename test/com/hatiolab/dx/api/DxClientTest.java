package com.hatiolab.dx.api;

import java.net.InetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hatiolab.dx.mplexer.EventMultiplexer;
import com.hatiolab.dx.net.DiscoveryListener;
import com.hatiolab.dx.net.PacketEventListener;
import com.hatiolab.dx.sample.client.Client.PacketClientListener;

public class DxClientTest {

	DxClient client;
	PacketEventListener packetEventListener = new PacketClientListener();
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		try {
			EventMultiplexer mplexer = EventMultiplexer.getInstance();
			
			client = new DxClient(mplexer, 3456, new DiscoveryListener() {
				@Override
				public void onFoundServer(InetAddress address, int port) {
					client.startPacketClient(address, port, packetEventListener);
				}
			});
		
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
