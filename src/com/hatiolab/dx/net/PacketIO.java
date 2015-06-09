package com.hatiolab.dx.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

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
import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;
import com.hatiolab.dx.packet.Packet;

public class PacketIO {
	static final protected Header header = new Header();
	static final protected ByteBuffer headerBuffer = ByteBuffer.allocate(8);
	static final protected ByteBuffer dataBuffer = ByteBuffer.allocate(2 * 1024 * 1024);
	
	public static int read(SocketChannel channel, ByteBuffer buffer) throws IOException {
		int remaining = buffer.remaining();
		int sz = channel.read(buffer);
		
		if(sz != remaining)
			throw new IOException("Can't read all remainings (" + sz + " of " + remaining + ")");
		
		return sz;
	}
	
	public static int write(SocketChannel channel, ByteBuffer buffer) throws IOException {
		int remaining = buffer.remaining();
		int sz = channel.write(buffer);
		
		if(sz != remaining)
			throw new IOException("Can't write all remainings (" + sz + " of " + remaining + ")");
		
		return sz;
	}

	public static Header parseHeader(SocketChannel channel) throws Exception {

		headerBuffer.clear();

		read(channel, headerBuffer);
		
		header.unmarshalling(headerBuffer.array(), 0);
		
		return header;
	}
	
	public static Data parseData(SocketChannel channel, Header header) throws Exception {

		long dataLength = 0;
		
		dataLength = header.getLen() - header.getByteLength();
		
		if(dataLength > 0) {
			dataBuffer.limit((int)dataLength);
			dataBuffer.position(0);

			read(channel, dataBuffer);
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
		
		return data;
	}

	public static void sendPacket(SocketChannel channel, Header header, Data data) throws IOException {
		Packet packet = new Packet(header, data);
		packet.marshalling(dataBuffer.array(), 0);
		
		dataBuffer.position(0);
		dataBuffer.limit(packet.getByteLength());
		
		/* Send response */
		write(channel, dataBuffer);
	}
}
