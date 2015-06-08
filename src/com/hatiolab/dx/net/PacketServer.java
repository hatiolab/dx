package com.hatiolab.dx.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.hatiolab.dx.api.EventListener;
import com.hatiolab.dx.data.ByteArray;
import com.hatiolab.dx.data.ByteString;
import com.hatiolab.dx.data.F32Array;
import com.hatiolab.dx.data.FileInfo;
import com.hatiolab.dx.data.FileInfoArray;
import com.hatiolab.dx.data.FilePartial;
import com.hatiolab.dx.data.FilePartialQuery;
import com.hatiolab.dx.data.Primitive;
import com.hatiolab.dx.data.S16Array;
import com.hatiolab.dx.data.S32Array;
import com.hatiolab.dx.data.Stream;
import com.hatiolab.dx.data.U16Array;
import com.hatiolab.dx.data.U32Array;
import com.hatiolab.dx.mplexer.SelectableHandler;
import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;

public class PacketServer {
	
	protected EventListener eventListener = null;
	protected int port;
	
	protected ServerSocketChannel serverSocketChannel;

	protected Header header = new Header();
	protected ByteBuffer headerBuffer = ByteBuffer.allocate(header.getByteLength());
	protected ByteBuffer dataBuffer = ByteBuffer.allocateDirect(2 * 1024 * 1024);

	protected SelectableHandler selectableHandler = new SelectableHandler() {
		@Override
		public void onSelected(SelectionKey key) {
			try {
				if(key.isAcceptable()) {
					SocketChannel accepted = serverSocketChannel.accept();
					
					SelectionKey registered = accepted.register(key.selector(), SelectionKey.OP_READ);
					registered.attach(this);
				}
				if(key.isReadable()) {

					SocketChannel channel = (SocketChannel)key.channel();

					long dataLength = 0;

					channel.read(headerBuffer);
					headerBuffer.flip();
					
					header.unmarshalling(headerBuffer.array(), 0);
					
					dataLength = header.getLen() - header.getByteLength();
					
					if(dataLength > 0) {
						dataBuffer.limit((int)dataLength);
						dataBuffer.position(0);

						channel.read(dataBuffer);
						dataBuffer.flip();
					}

					Data data = null;
					
					switch(header.getDataType()) {
					case Data.TYPE_NONE :
						data = new Data();
						break;
					case Data.TYPE_PRIMITIVE :
						data = new Primitive();
						break;
					case Data.TYPE_U8_ARRAY	:
					case Data.TYPE_S8_ARRAY	:
						data = new ByteArray();
						break;
					case Data.TYPE_U16_ARRAY :
						data = new U16Array();
						break;
					case Data.TYPE_S16_ARRAY :
						data = new S16Array();
						break;
					case Data.TYPE_U32_ARRAY :
						data = new U32Array();
						break;
					case Data.TYPE_S32_ARRAY :
						data = new S32Array();
						break;
					case Data.TYPE_F32_ARRAY :
						data = new F32Array();
						break;
					case Data.TYPE_STRING :
						data = new ByteString();
						break;

					case Data.TYPE_FILEINFO	:
						data = new FileInfo();
						break;
					case Data.TYPE_FILEINFO_ARRAY :
						data = new FileInfoArray();
						break;
					case Data.TYPE_FILE_PARTIAL_QUERY :
						data = new FilePartialQuery();
						break;
					case Data.TYPE_FILE_PARTIAL	:
						data = new FilePartial();
						break;
					case Data.TYPE_STREAM	:
						data = new Stream();
						break;
					}

					data.unmarshalling(dataBuffer.array(), 0);

					eventListener.onDxEvent(header, data);					
				}
			} catch(Exception e) {
				e.printStackTrace();
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
