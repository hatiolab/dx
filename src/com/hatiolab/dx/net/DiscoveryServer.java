package com.hatiolab.dx.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import com.hatiolab.dx.data.Primitive;
import com.hatiolab.dx.mplexer.SelectableHandler;
import com.hatiolab.dx.packet.Code;
import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;
import com.hatiolab.dx.packet.Packet;
import com.hatiolab.dx.packet.Type;

public class DiscoveryServer {
	
	public static final int DEFAULT_PORT_NUMBER = 2015;
	
	protected DatagramChannel channel;
	
	protected int discoveryServerPort;
	protected int packetServerPort;
	
	protected ByteBuffer buffer = ByteBuffer.allocate(12);
	protected Header header = new Header();

	protected SelectableHandler selectableHandler = new SelectableHandler() {
		@Override
		public void onSelected(SelectionKey key) {
			try {
				buffer.clear();
				InetSocketAddress addr = (InetSocketAddress)channel.receive(buffer);
				buffer.flip();

				/* Header unmarshalling */
				header.unmarshalling(buffer);

				if(0 == header.getType() && Data.TYPE_PRIMITIVE == header.getDataType() && 0 == header.getCode()) {
					// Should be discovery packet

					/* Data unmarshalling */
					Primitive data = new Primitive();
					data.unmarshalling(buffer);
					int clientport = data.getS32();
					
					header.setType(Type.DX_PACKET_TYPE_DISCOVERY);
					header.setCode((byte)Code.DX_DISCOVERY_RESPONSE);
					data.setS32(packetServerPort);
					
					Packet resp = new Packet(header, data);
					PacketIO.sendPacketTo(channel, resp, new InetSocketAddress(addr.getHostName(), clientport));
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				key.cancel();
			}
		}
	};

	public DiscoveryServer(int discoveryServerPort, int packetServerPort) throws IOException {
		this.packetServerPort = packetServerPort;
		
		channel = DatagramChannel.open();
		channel.configureBlocking(false);
		
		channel.socket().setBroadcast(true);
		channel.socket().bind(new InetSocketAddress("0.0.0.0", discoveryServerPort));
		channel.socket().setReuseAddress(true);
		
		this.discoveryServerPort = channel.socket().getLocalPort();
	}
	
	public void close() throws IOException {
		channel.close();
		channel = null;
	}
	
	public SelectableChannel getSelectableChannel() {
		return channel;
	}

	public SelectableHandler getSelectableHandler() {
		return selectableHandler;
	}
}
