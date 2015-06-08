package com.hatiolab.dx.net;

import static org.junit.Assert.assertFalse;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hatiolab.dx.api.EventListener;
import com.hatiolab.dx.mplexer.EventMultiplexer;
import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;
import com.hatiolab.dx.packet.Type;

public class PacketServerTest {

	EventMultiplexer mplexer;

	PacketServer packetServer;
	PacketClient packetClient;
	
	class SampleEventListener implements EventListener {

		@Override
		public void onEvent(SocketChannel channel, Header header, Data data) {
			try {
				System.out.println("HELLO, SERVER");			

				packetServer.sendPacket(channel, header, data); // Send Back..
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onConnected(SocketChannel channel) {
			
		}
		
		@Override
		public void onDisconnected(SocketChannel channel) {
			
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
			int i = 0;
			for(i = 0;i < 10;i++) {
				mplexer.poll(1000);
				
				if(i == 1) {
					packetClient = new PacketClient(new EventListener() {

						@Override
						public void onEvent(SocketChannel channel, Header header, Data data) {
							System.out.println("HELLO, CLIENT");			
						}
						
						@Override
						public void onConnected(SocketChannel channel) {
							Header header = new Header();
							header.setType(Type.DX_PACKET_TYPE_HB);
							header.setCode((byte)1);
							header.setDataType(Data.TYPE_NONE);
							
							try {
								packetClient.sendPacket(header, null);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
						@Override
						public void onDisconnected(SocketChannel channel) {
							
						}
						
					}, "localhost", 2015);
					
					SelectableChannel channel = packetClient.getSelectableChannel();
					SelectionKey key = channel.register(mplexer.getSelector(), SelectionKey.OP_CONNECT);
					key.attach(packetClient.getSelectableHandler());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
}
