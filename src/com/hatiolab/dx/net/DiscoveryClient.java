package com.hatiolab.dx.net;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import com.hatiolab.dx.data.Primitive;
import com.hatiolab.dx.mplexer.SelectableHandler;
import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;
import com.hatiolab.dx.packet.Packet;

public class DiscoveryClient {

	protected DatagramSocket socket;
	protected DatagramChannel channel;
	
	protected int discoveryServerPort;
	
	protected DiscoveryListener discoveryListener;
	
	protected byte[] headerBuf = new byte[128];
	protected byte[] dataBuf = new byte[128];
	protected byte[] discoveryBuf = new byte[128];
	protected ByteBuffer buffer = ByteBuffer.allocate(100);

	protected SelectableHandler selectableHandler = new SelectableHandler() {
		@Override
		public void onSelected(SelectionKey key) {
			try {
				buffer.position(0);
				InetSocketAddress addr = (InetSocketAddress)channel.receive(buffer);
				buffer.flip();
				
				/* Header unmarshalling */
				Header header = new Header();
				buffer.get(headerBuf, 0, header.getByteLength());
				header.unmarshalling(headerBuf, 0);
				
				long dataLength = header.getLen() - header.getByteLength();
				if(dataLength > 0) {
					buffer.get(dataBuf, 0, (int)dataLength);
				}

				if(Data.TYPE_PRIMITIVE == header.getDataType() && 1 == header.getCode()) {
					// Must be discovery response packet

					/* Data unmarshalling */
					Primitive data = new Primitive();
					data.unmarshalling(dataBuf, 0);
					int serverport = data.getS32();
					
					DiscoveryClient.this.discoveryListener.onFoundServer(addr.getAddress(), serverport);
				}
				
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
		
		header.setType(0);
		header.setCode((byte)0);
		header.setDataType(Data.TYPE_PRIMITIVE);
		
		data.setS32(channel.socket().getLocalPort());
		
		Packet resp = new Packet(header, data);
		resp.marshalling(discoveryBuf, 0);
		
		/* Send response */
		ByteBuffer buffer = ByteBuffer.wrap(discoveryBuf, 0, resp.getByteLength());
		channel.send(buffer, new InetSocketAddress("255.255.255.255", this.discoveryServerPort));
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
