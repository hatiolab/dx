package com.hatiolab.dx.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.hatiolab.dx.api.EventListener;
import com.hatiolab.dx.mplexer.SelectableHandler;
import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;

public class PacketClient {
	
	protected EventListener eventListener = null;
	protected int port;
	
	protected SocketChannel clientSocketChannel;
	protected boolean connected = false;

	protected SelectableHandler selectableHandler = new SelectableHandler() {
		@Override
		public void onSelected(SelectionKey key) {
			try {
				int interops = key.interestOps();
				
				if(key.isConnectable() && (interops & SelectionKey.OP_CONNECT) != 0) {
					SocketChannel channel = (SocketChannel)key.channel();
					if(channel.isConnectionPending()) {
						if(channel.finishConnect()) {
							
							PacketClient.this.connected = true;
							
							eventListener.onConnected(channel);

							key.interestOps(SelectionKey.OP_READ);
						}
					}

				}
				if(key.isReadable()) {

					SocketChannel channel = (SocketChannel)key.channel();

					Header header = PacketIO.parseHeader(channel);
					Data data = PacketIO.parseData(channel, header);

					eventListener.onEvent(header, data);					
		
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
		connected = false;
	}
	
	public SelectableChannel getSelectableChannel() {
		return clientSocketChannel;
	}

	public SelectableHandler getSelectableHandler() {
		return selectableHandler;
	}
	
	public boolean isConnected() {
		return connected;
	}
}
