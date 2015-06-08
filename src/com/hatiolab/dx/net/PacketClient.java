package com.hatiolab.dx.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.hatiolab.dx.api.EventListener;
import com.hatiolab.dx.mplexer.SelectableHandler;
import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;
import com.hatiolab.dx.packet.Packet;

public class PacketClient {
	
	protected EventListener eventListener = null;
	protected int port;
	
	protected SocketChannel clientSocketChannel;

	protected Header header = new Header();
	protected ByteBuffer headerBuffer = ByteBuffer.allocate(header.getByteLength());
	protected ByteBuffer dataBuffer = ByteBuffer.allocate(2 * 1024 * 1024);

	protected SelectableHandler selectableHandler = new SelectableHandler() {
		@Override
		public void onSelected(SelectionKey key) {
			try {
				int interops = key.interestOps();
				
				if(key.isConnectable() && (interops & SelectionKey.OP_CONNECT) != 0) {
					SocketChannel channel = (SocketChannel)key.channel();
					if(channel.isConnectionPending()) {
						if(channel.finishConnect()) {
							eventListener.onConnected(channel);

							key.interestOps(SelectionKey.OP_READ);
						}
					}

				}
				if(key.isReadable()) {

					SocketChannel channel = (SocketChannel)key.channel();

					Header header = PacketReader.parseHeader(channel);
					Data data = PacketReader.parseData(channel, header);

					eventListener.onEvent(channel, header, data);					
		
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	public PacketClient(EventListener eventListener, String host, int port) throws IOException {
		this.eventListener = eventListener;
		this.port = port;
		
		clientSocketChannel = SocketChannel.open();
		clientSocketChannel.configureBlocking(false);
		clientSocketChannel.connect(new InetSocketAddress(host, this.port));
	}

	public void close() throws IOException {
		clientSocketChannel.close();
		clientSocketChannel = null;
	}
	
	public SelectableChannel getSelectableChannel() {
		return clientSocketChannel;
	}

	public SelectableHandler getSelectableHandler() {
		return selectableHandler;
	}
	
	public void sendPacket(Header header, Data data) throws Exception {
		Packet packet = new Packet(header, data);
		packet.marshalling(dataBuffer.array(), 0);
		
		dataBuffer.limit(packet.getByteLength());
		dataBuffer.position(0);
		
		/* Send response */
		clientSocketChannel.write(dataBuffer);
	}
}
