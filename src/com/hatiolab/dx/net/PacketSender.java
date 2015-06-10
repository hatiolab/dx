package com.hatiolab.dx.net;

import java.io.IOException;

import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;
import com.hatiolab.dx.packet.Packet;

public interface PacketSender {

	public void sendPacket(Header header, Data data) throws IOException;
	public void sendPacket(Packet packet) throws IOException;
}
