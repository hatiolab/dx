package com.hatiolab.dx.sample.client.Client;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.hatiolab.dx.data.Primitive;
import com.hatiolab.dx.net.PacketEventListener;
import com.hatiolab.dx.net.PacketIO;
import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;
import com.hatiolab.dx.packet.Type;

public class PacketClientListener implements PacketEventListener {

	public PacketClientListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onEvent(SocketChannel channel, Header header, Data data) throws IOException {
		PacketIO.sendPacket(channel, header, data);
		System.out.println("HELLO, CLIENT");			
	}
	
	@Override
	public void onConnected(SocketChannel channel) {
		
		Header header = new Header();
		header.setType(Type.DX_PACKET_TYPE_HB);
		header.setCode((byte)1);
		header.setDataType(Data.TYPE_PRIMITIVE);
		
		Primitive data = new Primitive();
		data.setF32(0.1f);
		
		try {
			PacketIO.sendPacket(channel, header, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDisconnected(SocketChannel channel) {			
	}
}
