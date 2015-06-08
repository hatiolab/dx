package com.hatiolab.dx.net;

import static org.junit.Assert.assertFalse;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hatiolab.dx.mplexer.EventMultiplexer;

public class DiscoveryServerTest {

	EventMultiplexer mplexer;
	DiscoveryServer discoveryServer;
	DiscoveryClient discoveryClient;

	@Before
	public void setUp() throws Exception {
		mplexer = new EventMultiplexer();
		discoveryServer = new DiscoveryServer();
		discoveryClient = new DiscoveryClient();
		
		SelectableChannel channel = discoveryServer.getSelectableChannel();
		SelectionKey key = channel.register(mplexer.getSelector(), SelectionKey.OP_READ);
		key.attach(discoveryServer.getSelectableHandler());
		
		channel = discoveryClient.getSelectableChannel();
		key = channel.register(mplexer.getSelector(), SelectionKey.OP_READ);
		key.attach(discoveryClient.getSelectableHandler());		
	}

	@After
	public void tearDown() throws Exception {
		mplexer = null;
		discoveryServer = null;
	}

	@Test
	public void test() {
		try {
			for(int i = 0;i < 10;i++) {
				mplexer.poll(1000);
				
				discoveryClient.sendDiscoveryPacket();
			}
			
			mplexer.close();
			discoveryClient.close();
			discoveryServer.close();			
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}

}
