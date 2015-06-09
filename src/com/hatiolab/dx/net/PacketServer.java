package com.hatiolab.dx.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.hatiolab.dx.api.EventListener;
import com.hatiolab.dx.mplexer.SelectableHandler;
import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;

public class PacketServer {
	
	protected EventListener eventListener = null;
	protected int port;
	
	protected ServerSocketChannel serverSocketChannel;

	protected SelectableHandler selectableHandler = new SelectableHandler() {
		@Override
		public void onSelected(SelectionKey key) {
			try {
				if(key.isAcceptable()) {
					SocketChannel accepted = ((ServerSocketChannel)key.channel()).accept();
					if(accepted != null) {
						accepted.configureBlocking(false);
						
						SelectionKey registered = accepted.register(key.selector(), SelectionKey.OP_READ);
						registered.attach(this);
						
						eventListener.onConnected(accepted);
						System.out.println("ACCEPTED");
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
				key.cancel();
			}
		}
	};
	
	public PacketServer(EventListener eventListener, int port) throws IOException {
		this.eventListener = eventListener;
		this.port = port;
		
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.socket().bind(new InetSocketAddress("0.0.0.0", this.port));
		serverSocketChannel.socket().setReuseAddress(true);
	}

	public void close() throws IOException {
		serverSocketChannel.close();
		serverSocketChannel = null;
	}
	
	public SelectableChannel getSelectableChannel() {
		return serverSocketChannel;
	}

	public SelectableHandler getSelectableHandler() {
		return selectableHandler;
	}

}
