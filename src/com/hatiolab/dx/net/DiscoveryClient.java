package com.hatiolab.dx.net;

import java.io.IOException;
import java.net.InetSocketAddress;
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

public class DiscoveryClient {

	protected DatagramChannel channel;
	
	protected int discoveryServerPort;
	
	protected DiscoveryListener discoveryListener;
	
	protected SelectableHandler selectableHandler = new SelectableHandler() {
		PacketIO.PacketReceivedListener listener = new PacketIO.PacketReceivedListener() {

			@Override
			public void onReceive(DatagramChannel channel, Header header, Data data, InetSocketAddress addr) throws Exception {
				if(Data.TYPE_PRIMITIVE == header.getDataType() && 1 == header.getCode()) {
					// Should be discovery response packet

					int serverport = ((Primitive)data).getS32();
					
					DiscoveryClient.this.discoveryListener.onFoundServer(addr.getAddress(), serverport);
				}
			}
		};
		
		@Override
		public void onSelected(SelectionKey key) {
			try {
				PacketIO.receivePacket(channel, listener);
			} catch (Exception e) {
				e.printStackTrace();
				key.cancel();
			}
		}
	};
	
	public void sendDiscoveryPacket() throws Exception {
		/* Response packet marshalling */
		Header header = new Header();
		Primitive data = new Primitive();
		
		header.setType(Type.DX_PACKET_TYPE_DISCOVERY);
		header.setCode((byte)Code.DX_DISCOVERY_REQUEST);
		header.setDataType(Data.TYPE_PRIMITIVE);
		
		data.setS32(channel.socket().getLocalPort());
		
		Packet resp = new Packet(header, data);
		PacketIO.sendPacketTo(channel, resp, new InetSocketAddress("255.255.255.255", this.discoveryServerPort));
	}

	public DiscoveryClient(DiscoveryListener discoveryListener) throws IOException {
		this(discoveryListener, DiscoveryServer.DEFAULT_PORT_NUMBER, 0);
	}

	public DiscoveryClient(DiscoveryListener discoveryListener, int discoveryServerPort, int discoveryClientPort) throws IOException {
		this.discoveryListener = discoveryListener;

		this.discoveryServerPort = discoveryServerPort;
		
		channel = DatagramChannel.open();
		channel.configureBlocking(false);
		
		channel.socket().setBroadcast(true);
		channel.socket().bind(new InetSocketAddress("0.0.0.0", discoveryClientPort));
		channel.socket().setReuseAddress(true);
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
