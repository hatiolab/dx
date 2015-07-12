package com.hatiolab.dx.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.hatiolab.dx.mplexer.SelectableHandler;
import com.hatiolab.dx.packet.Packet;

public class PacketClient {
	public static final String TAG = "PacketClient";
	
	public static final int DEFAULT_SOCKET_RCV_BUF_SIZE = 1024000 / 2; 
	public static final int DEFAULT_SOCKET_SND_BUF_SIZE = 1024000 / 2;
	
	protected PacketEventListener eventListener = null;
	protected int port;
	
	protected SocketChannel clientSocketChannel;
	protected boolean connected = false;

	protected SelectableHandler selectableHandler = new SelectableHandler() {
		@Override
		public void onSelected(SelectionKey key) {
			try {
				if(key.isConnectable()) {
					
					SocketChannel channel = (SocketChannel)key.channel();
					if(channel.isConnectionPending()) {
						if(channel.finishConnect()) {
							
							PacketClient.this.connected = true;

							key.interestOps(SelectionKey.OP_READ);
							
							eventListener.onConnected(channel);
						}
					}
				}
				
				if(key.isReadable()) {
					SocketChannel channel = (SocketChannel)key.channel();
					Packet packet = PacketIO.receivePacket(channel);

					if(packet != null)
						eventListener.onEvent(channel, packet.getHeader(), packet.getData());
				}
				
				if (key.isWritable()) {
					PacketIO.sendQueuedPackets(key);
				}
			} catch(Exception e) {
				e.printStackTrace();
				try {
					close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				key.cancel();
			}
		}
	};
	
	public PacketClient(PacketEventListener eventListener, String host, int port) throws IOException {
		this.eventListener = eventListener;
		this.port = port;
		
		clientSocketChannel = SocketChannel.open();
		clientSocketChannel.configureBlocking(false);
		clientSocketChannel.connect(new InetSocketAddress(host, this.port));
		
		clientSocketChannel.socket().setTcpNoDelay(true);
		clientSocketChannel.socket().setKeepAlive(true);
		clientSocketChannel.socket().setReceiveBufferSize(DEFAULT_SOCKET_RCV_BUF_SIZE);
		clientSocketChannel.socket().setSendBufferSize(DEFAULT_SOCKET_SND_BUF_SIZE);
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
