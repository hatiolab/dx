package com.hatiolab.dx.net;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.net.InetAddress;
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
	DiscoveryServer discoveryServer;
	DiscoveryClient discoveryClient;

	class ServerEventListener implements EventListener {
		SocketChannel channel;
		
		@Override
		public void onEvent(Header header, Data data) {
			try {
				System.out.println("HELLO, SERVER");			

				PacketIO.sendPacket(this.channel, header, data); // Send Back..
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onConnected(SocketChannel channel) {
			this.channel = channel;
		}
		
		@Override
		public void onDisconnected(SocketChannel channel) {			
		}
		
	}
	
	class ClientEventListener implements EventListener {
		SocketChannel channel;
		
		@Override
		public void onEvent(Header header, Data data) {
			try {
				PacketIO.sendPacket(this.channel, header, data);
			} catch (Exception e) {
				e.printStackTrace();
			} // Send Back..
			System.out.println("HELLO, CLIENT");			
		}
		
		@Override
		public void onConnected(SocketChannel channel) {
			this.channel = channel;
			
			Header header = new Header();
			header.setType(Type.DX_PACKET_TYPE_HB);
			header.setCode((byte)1);
			header.setDataType(Data.TYPE_NONE);
			
			try {
				PacketIO.sendPacket(this.channel, header, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onDisconnected(SocketChannel channel) {			
		}
	}
	
	class ServerDiscoveryListener implements DiscoveryListener {

		@Override
		public void onFoundServer(InetAddress address, int port) {
			try {
				packetClient = new PacketClient(new ClientEventListener(), address.getHostAddress(), port);

				SelectableChannel channel = packetClient.getSelectableChannel();
				SelectionKey key = channel.register(mplexer.getSelector(), SelectionKey.OP_CONNECT);
				key.attach(packetClient.getSelectableHandler());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Before
	public void setUp() throws Exception {
		mplexer = new EventMultiplexer();

		discoveryServer = new DiscoveryServer();
		discoveryClient = new DiscoveryClient(new ServerDiscoveryListener());
		
		SelectableChannel channel = discoveryServer.getSelectableChannel();
		SelectionKey key = channel.register(mplexer.getSelector(), SelectionKey.OP_READ);
		key.attach(discoveryServer.getSelectableHandler());
		
		channel = discoveryClient.getSelectableChannel();
		key = channel.register(mplexer.getSelector(), SelectionKey.OP_READ);
		key.attach(discoveryClient.getSelectableHandler());		
		
		packetServer = new PacketServer(new ServerEventListener(), 2015);
		
		channel = packetServer.getSelectableChannel();
		key = channel.register(mplexer.getSelector(), SelectionKey.OP_ACCEPT);
		key.attach(packetServer.getSelectableHandler());
	}

	@After
	public void tearDown() throws Exception {
		mplexer.close();
		packetServer.close();
		discoveryClient.close();
		discoveryServer.close();
	}

	@Test
	public void test() {
		try {
			int i = 0;
			
			discoveryClient.sendDiscoveryPacket();
			
			for(i = 0;i < 10;i++) {
				mplexer.poll(1000);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
}
