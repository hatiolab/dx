package com.hatiolab.dx.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.hatiolab.dx.mplexer.SelectableHandler;
import com.hatiolab.dx.packet.Packet;

public class PacketServer {
	public static final String TAG = "PacketServer";
	
	public static final int DEFAULT_SOCKET_RCV_BUF_SIZE = 1024000 / 2;
	public static final int DEFAULT_SOCKET_SND_BUF_SIZE = 1024000 / 2;
		
	protected PacketEventListener eventListener = null;
	protected int servicePort;
	
	protected ServerSocketChannel serverSocketChannel;

	protected SelectableHandler selectableHandler = new SelectableHandler() {
		@Override
		public void onSelected(SelectionKey key) {
			try {
				if(key.isAcceptable()) {
					SocketChannel accepted = ((ServerSocketChannel)key.channel()).accept();
					if(accepted != null) {
						accepted.configureBlocking(false);

						accepted.socket().setTcpNoDelay(true);
						accepted.socket().setKeepAlive(true);
						accepted.socket().setReceiveBufferSize(DEFAULT_SOCKET_RCV_BUF_SIZE);
						accepted.socket().setSendBufferSize(DEFAULT_SOCKET_SND_BUF_SIZE);
						
						SelectionKey registered = accepted.register(key.selector(), SelectionKey.OP_READ);
						registered.attach(this);
						
						SessionManager.register(accepted);
						
						/* callback to listener */
						eventListener.onConnected(accepted);
						System.out.println("ACCEPTED");
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
					key.channel().close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				key.cancel();
			}
		}
	};
	
	public PacketServer(PacketEventListener eventListener, int port) throws IOException {
		this.eventListener = eventListener;
		
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.socket().bind(new InetSocketAddress("0.0.0.0", port));
		serverSocketChannel.socket().setReuseAddress(true);
		
		this.servicePort = serverSocketChannel.socket().getLocalPort();
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

	public int getServicePort() {
		return servicePort;
	}

}
