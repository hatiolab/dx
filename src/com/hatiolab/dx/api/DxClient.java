package com.hatiolab.dx.api;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import com.hatiolab.dx.mplexer.EventMultiplexer;
import com.hatiolab.dx.net.DiscoveryClient;
import com.hatiolab.dx.net.DiscoveryListener;
import com.hatiolab.dx.net.PacketClient;
import com.hatiolab.dx.net.PacketEventListener;
import com.hatiolab.dx.net.PacketSender;
import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;
import com.hatiolab.dx.packet.Packet;

public class DxClient implements PacketSender {
	
	protected EventMultiplexer mplexer;

	protected PacketClient packetClient;
	protected DiscoveryClient discoveryClient;
	
	public DxClient() {}

	public void start(int discoveryServicePort, DiscoveryListener eventListener) throws Exception {
		mplexer = new EventMultiplexer();

		discoveryClient = new DiscoveryClient(eventListener, discoveryServicePort, 0);
		
		SelectableChannel channel = discoveryClient.getSelectableChannel();
		SelectionKey key = channel.register(mplexer.getSelector(), SelectionKey.OP_READ);
		key.attach(discoveryClient.getSelectableHandler());	
		
		while(true) {
			mplexer.poll(1000);

			if(packetClient == null || packetClient.isConnected() == false) {
				discoveryClient.sendDiscoveryPacket();
			}
		}
	}

	public void stop() throws Exception {
		mplexer.close();
		discoveryClient.close();
	}
	
	public void startPacketClient(InetAddress address, int port, PacketEventListener eventListener) {
		if(packetClient != null && packetClient.isConnected())
			return;
		
		try {
			packetClient = new PacketClient(eventListener, address.getHostAddress(), port);

			SelectableChannel channel = packetClient.getSelectableChannel();
			SelectionKey key = channel.register(mplexer.getSelector(), SelectionKey.OP_CONNECT);
			key.attach(packetClient.getSelectableHandler());
			
		} catch (IOException e) {
			e.printStackTrace();
			try {
				packetClient.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			packetClient = null;
		}
	}
	
	@Override
	public void sendPacket(Header header, Data data) throws IOException {
		if(packetClient == null)
			throw new IOException("Packet Client is not ready.");
		
		packetClient.sendPacket(header, data);
	}

	@Override
	public void sendPacket(Packet packet) throws IOException {
		if(packetClient == null)
				throw new IOException("Packet Client is not ready.");
			
		packetClient.sendPacket(packet);
	}
}
