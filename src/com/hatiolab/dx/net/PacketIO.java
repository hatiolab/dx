package com.hatiolab.dx.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.util.Log;

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
import com.hatiolab.dx.mplexer.EventMultiplexer;
import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;
import com.hatiolab.dx.packet.Packet;

public class PacketIO {
	static final protected Header header = new Header();
	static final protected ByteBuffer headerBuffer = ByteBuffer.allocate(8);

	static private class QueuedBuffer {
		ByteBuffer buffer;
		boolean discardable;
		QueuedBuffer(ByteBuffer buffer, boolean discardable) {
			this.buffer = buffer;
			this.discardable = discardable;
		}
	}
	
	static WeakHashMap<SocketChannel, Queue<QueuedBuffer>> packetQueue = new WeakHashMap<SocketChannel, Queue<QueuedBuffer>>();

	static int writePos = 0;
	
	public static int read(SocketChannel channel, ByteBuffer buffer) throws IOException {
		int nread = channel.read(buffer);
		if(0 > nread)
			throw new IOException("Peer closed.");
		return nread;
	}
	
	public static Header parseHeader(SocketChannel channel) throws Exception {

		headerBuffer.clear();
		read(channel, headerBuffer);
		headerBuffer.flip();
		header.unmarshalling(headerBuffer);
		
		return header;
	}
	
	public static Header parseHeader(ByteBuffer buf) throws Exception {
		header.unmarshalling(buf);
		
		return header;
	}
	
	public static Data parseData(ByteBuffer buf, Header header) throws Exception {
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

		data.unmarshalling(buf);
		
		return data;
	}

	public static void sendPacket(SocketChannel channel, Packet packet, boolean discardable) throws IOException {
		
		Queue<QueuedBuffer> queue = packetQueue.get(channel);			
		if (queue == null) {
			queue = new ConcurrentLinkedQueue<QueuedBuffer>();
			packetQueue.put(channel, queue);
		}
		
		ByteBuffer buf = ByteBuffer.allocate(packet.getByteLength());
		packet.marshalling(buf);
		buf.flip();
		queue.add(new QueuedBuffer(buf, discardable));
		
		Selector selector = EventMultiplexer.getInstance().getSelector();
		SelectionKey key = channel.keyFor(selector);
		int i = key.interestOps();
		key.interestOps(i | SelectionKey.OP_WRITE);
		EventMultiplexer.getInstance().wakeup();
	}
	
	public static void sendPacket(SocketChannel channel, Packet packet) throws IOException {
		sendPacket(channel, packet, false);
	}
	
	public static void sendPacket(SocketChannel channel, Header header, Data data) throws IOException {
		sendPacket(channel, new Packet(header, data), false);
	}
	
	public static void sendPacket(SocketChannel channel, Header header, Data data, boolean discardable) throws IOException {
		sendPacket(channel, new Packet(header, data), discardable);
	}
	
	public static void sendPacketTo(DatagramChannel channel, Packet packet, SocketAddress to) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(packet.getByteLength());
		packet.marshalling(buf);
		buf.flip();
		
		channel.send(buf, to);
	}
	
	public static void sendPacketTo(DatagramChannel channel, Header header, Data data, SocketAddress to) throws IOException {
		sendPacketTo(channel, new Packet(header, data), to);
	}
	
	protected static int sendQueuedPackets(SelectionKey key) throws IOException {
		EventMultiplexer.assertPreemption();
		
		SocketChannel channel = (SocketChannel)key.channel();
		
		Queue<QueuedBuffer> queue = packetQueue.get(channel);
		if (queue == null) {
			int i = key.interestOps();
			key.interestOps(i & ~SelectionKey.OP_WRITE);
			
			return 0;
		}
		
		while(true) {
			QueuedBuffer qb = queue.peek();
			
			if(qb == null) {
				int i = key.interestOps();
				key.interestOps(i & ~SelectionKey.OP_WRITE);
				
				return 0;
			}
			
			ByteBuffer tmpBuffer = qb.buffer;
			
			if(qb.discardable && queue.size() > 3 && tmpBuffer.position() == 0) {
				queue.poll();
				Log.d("discard", "discard");
				continue;
			}

			channel.write(tmpBuffer);
			if (tmpBuffer.hasRemaining()) {
				return 0;
			}
			
			queue.poll();
		}
	}
}
