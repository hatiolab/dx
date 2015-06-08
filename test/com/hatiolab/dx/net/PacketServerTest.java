package com.hatiolab.dx.net;

import static org.junit.Assert.assertFalse;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hatiolab.dx.api.EventListener;
import com.hatiolab.dx.mplexer.EventMultiplexer;
import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;

public class PacketServerTest {

	PacketServer packetServer;
	EventMultiplexer mplexer;
	
	class SampleEventListener implements EventListener {

		@Override
		public void onDxEvent(Header header, Data data) {
			// TODO Auto-generated method stub
			
		}
		
	}

	@Before
	public void setUp() throws Exception {
		mplexer = new EventMultiplexer();
		packetServer = new PacketServer(new SampleEventListener(), 2015);
		
		SelectableChannel channel = packetServer.getSelectableChannel();
		SelectionKey key = channel.register(mplexer.getSelector(), SelectionKey.OP_ACCEPT);
		key.attach(packetServer.getSelectableHandler());
	}

	@After
	public void tearDown() throws Exception {
		mplexer.close();
		packetServer.close();
	}

	@Test
	public void test() {
		try {
			for(int i = 0;i < 3;i++) {
				mplexer.poll(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
}
