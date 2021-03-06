package com.hatiolab.dx.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.hatiolab.dx.mplexer.SelectableHandler;
import com.hatiolab.dx.packet.Code;
import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;
import com.hatiolab.dx.packet.Packet;
import com.hatiolab.dx.packet.Type;

public class PacketClient {
	public static final String TAG = "PacketClient";
	
//	public static final int DEFAULT_SOCKET_RCV_BUF_SIZE = 1024000 / 10; 
//	public static final int DEFAULT_SOCKET_SND_BUF_SIZE = 1024000 / 10;
	
	public static final int DEFAULT_SOCKET_RCV_BUF_SIZE = 8192 * 4;
	public static final int DEFAULT_SOCKET_SND_BUF_SIZE = 8192 * 4;
	
	protected PacketEventListener eventListener = null;
	protected int port;
	
	protected SocketChannel clientSocketChannel;
	protected boolean connected = false;

	protected SelectableHandler selectableHandler = new SelectableHandler() {
		@Override
		public void onSelected(SelectionKey key) {
			SocketChannel channel = (SocketChannel)key.channel();
			try {
				if(key.isConnectable()) {
					
					if(channel.isConnectionPending()) {
						if(channel.finishConnect()) {
							
							PacketClient.this.connected = true;

							key.interestOps(SelectionKey.OP_READ);
							
							SessionManager.register(channel);
							
							/* Send Connected Event to peer */
							Header header = new Header();
							header.setType(Type.DX_PACKET_TYPE_EVENT);
							header.setCode((byte)Code.DX_EVT_CONNECT);
							header.setDataType(Data.TYPE_NONE);
							
							try {
								PacketIO.sendPacket(channel, header, null);
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							/* callback */
							eventListener.onConnected(channel);
						}
					}
				}
				
				if(key.isReadable()) {
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
					close(channel);
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

	public void close(SocketChannel channel) throws IOException {
		if (clientSocketChannel != null) {
			clientSocketChannel.close();
			clientSocketChannel = null;
		}
		
		connected = false;
		eventListener.onDisconnected(channel);
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
