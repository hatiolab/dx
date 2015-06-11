package com.hatiolab.dx.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

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
import com.hatiolab.dx.net.PacketEventListener;
import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Code;
import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;
import com.hatiolab.dx.packet.Packet;
import com.hatiolab.dx.packet.Type;

public class DxConnect {
	public static final int DEFAULT_FILE_PARTIAL_MAX_SIZE = 500000;
	public static final int DEFAULT_SERVICE_PORT = 2015;
	public static final int DEFAULT_SOCKET_RCV_BUF_SIZE = 1024000; 
	public static final int DEFAULT_SOCKET_SND_BUF_SIZE = 1024000;
	
	public static final int STATE_STOPPED = 0;
	public static final int STATE_STARTED = 1;
	public static final int STATE_WANT_STOP = 2;
	
	private String hostname;
	private int port;
	private PacketEventListener eventListener;
	private int state;
	private int sendBufferSize = DEFAULT_SOCKET_SND_BUF_SIZE;
	private int recvBufferSize = DEFAULT_SOCKET_RCV_BUF_SIZE;
	
	private Socket socket;
	
	public DxConnect(String hostname) {
		this.hostname = hostname;
		this.port = DEFAULT_SERVICE_PORT;
	}
	
	public DxConnect(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	public void setRecvBufferSize(int recvBufferSize) {
		this.recvBufferSize = recvBufferSize;
	}

	public void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}

	public void setEventListener(PacketEventListener eventListener) {
		this.eventListener = eventListener;
	}
	
	public void start() {
		this.state = STATE_STARTED;
		
		while(this.state == STATE_STARTED) {
			try {
				this.connect();

				InputStream is = this.socket.getInputStream();
				
				Header header = new Header();
				byte[] headerBuf = new byte[header.getByteLength()];
				byte[] dataBuf = new byte[2 * 1024 * 1024];
				long dataLength = 0;
				
				while(this.state == STATE_STARTED) {
					try {
						Util.read(is, headerBuf, 8);
						header.unmarshalling(headerBuf, 0);
						
						dataLength = header.getLen() - header.getByteLength();
						
						if(dataLength > 0) {
							Util.read(is, dataBuf, (int)dataLength);
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

						data.unmarshalling(dataBuf, 0);

						eventListener.onEvent(null, header, data);

					} catch (Exception e) {
						e.printStackTrace();
						
						if(this.state == STATE_WANT_STOP) {
							this.state = STATE_STOPPED;
							return;
						} else {
							throw e;
						}
					}
				}
			} catch (Exception e) {
				try {
					this.disconnect();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				this.socket = null;
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void stop() {
		this.state = STATE_WANT_STOP;
		try {
			this.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void connect() throws IOException {
		this.socket = new Socket(this.hostname, this.port);
		
		this.socket.setReceiveBufferSize(recvBufferSize);
		this.socket.setSendBufferSize(sendBufferSize);
	}
	
	public void disconnect() throws IOException {
		if(this.socket == null)
			return;
		
		this.socket.close();
		this.socket = null;
		
		eventListener.onDisconnected(null);
	}
	
	public void sendPacket(Packet packet) throws IOException {
		if(this.socket == null)
			throw new IOException("Socket is not connected");
		
		int length = (int)packet.getByteLength();
		
		byte[] bytes = new byte[length];
		
		packet.getHeader().setLen(length);
		
		packet.marshalling(bytes, 0);
		
		Util.write(this.socket.getOutputStream(), bytes, length);
	}
	
	public void sendHeartBeat() throws Exception {
		final Packet packet = new Packet(Type.DX_PACKET_TYPE_HB, 0, null);
		sendPacket(packet);
	}
	
	public void sendGetFileList(String path) throws Exception {
		Packet packet = new Packet(Type.DX_PACKET_TYPE_FILE, Code.DX_FILE_GET_LIST, new ByteString(path));
		sendPacket(packet);
	}

	public void sendGetFile(String path, int begin, int end) throws Exception {
		FilePartialQuery query = new FilePartialQuery(path, begin, end);
		Packet packet = new Packet(Type.DX_PACKET_TYPE_FILE, Code.DX_FILE_GET, query);
		sendPacket(packet);
	}

	public void sendFile(String path, int total_len, int partial_len, int begin, int end, byte[] content) throws Exception {
		FilePartial partial = new FilePartial(path, total_len, partial_len, begin, end, content);
		Packet packet = new Packet(Type.DX_PACKET_TYPE_FILE, Code.DX_FILE, partial);
		sendPacket(packet);
	}

	public void sendDeleteFile(String path) throws Exception {
		Packet packet = new Packet(Type.DX_PACKET_TYPE_FILE, Code.DX_FILE_DELETE, new ByteString(path));
		sendPacket(packet);
	}
}
