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
	
	protected byte[] headerBuf = new byte[128];
	protected byte[] dataBuf = new byte[128];
	protected byte[] respBuf = new byte[128];
	protected ByteBuffer buffer = ByteBuffer.allocate(12);

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

				if(0 == header.getType() && Data.TYPE_PRIMITIVE == header.getDataType() && 0 == header.getCode()) {
					// Must be discovery packet

					/* Data unmarshalling */
					Primitive data = new Primitive();
					data.unmarshalling(dataBuf, 0);
					int clientport = data.getS32();
					
					header.setType(Type.DX_PACKET_TYPE_DISCOVERY);
					header.setCode((byte)Code.DX_DISCOVERY_RESPONSE);
					data.setS32(packetServerPort);
					
					/* Response packet marshalling */
					Packet resp = new Packet(header, data);
					resp.marshalling(respBuf, 0);
					
					/* Send response */
					ByteBuffer buffer = ByteBuffer.wrap(respBuf, 0, resp.getByteLength());
					channel.send(buffer, new InetSocketAddress(addr.getHostName(), clientport));
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public DiscoveryServer() throws IOException {
		this(DEFAULT_PORT_NUMBER);
	}
	
	public DiscoveryServer(int discoveryServerPort) throws IOException {
		/*
		 *  통상 discovery service port와 packet service port는 동일한 port number를 사용한다고 가정함
		 *  Port number는 동일하지만, Discovery Service는 UDP, Packet Service는 TCP를 사용함. 
		 */
		this(discoveryServerPort, discoveryServerPort);
	}
	
	public DiscoveryServer(int discoveryServerPort, int packetServerPort) throws IOException {
		this.discoveryServerPort = discoveryServerPort;
		this.packetServerPort = packetServerPort;
		
		channel = DatagramChannel.open();
		channel.configureBlocking(false);
		
		channel.socket().setBroadcast(true);
		channel.socket().bind(new InetSocketAddress("0.0.0.0", this.discoveryServerPort));
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
