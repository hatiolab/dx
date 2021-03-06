package com.hatiolab.dx.sample.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.hatiolab.dx.net.PacketEventListener;
import com.hatiolab.dx.net.PacketIO;
import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;

public class PacketServerListener implements PacketEventListener {

	public PacketServerListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onEvent(SocketChannel channel, Header header, Data data) throws IOException {
		System.out.println("HELLO, SERVER");			

		PacketIO.sendPacket(channel, header, data); // Send Back..
	}
	
	@Override
	public void onConnected(SocketChannel channel) {
	}
	
	@Override
	public void onDisconnected(SocketChannel channel) {			
	}

}
