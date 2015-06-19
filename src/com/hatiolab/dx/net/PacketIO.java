package com.hatiolab.dx.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
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
	private static final int MAX_PACKET_SIZE = 1024 * 1024;
	private static final byte[] DEFAULT_BROADCAST_ADDR = { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF };
	
	static private class QueuedBuffer {
		ByteBuffer buffer;
		boolean discardable;
		QueuedBuffer(ByteBuffer buffer, boolean discardable) {
			this.buffer = buffer;
			this.discardable = discardable;
		}
	}
	
	private static WeakHashMap<SocketChannel, Queue<QueuedBuffer>> sendBufferQMap = new WeakHashMap<SocketChannel, Queue<QueuedBuffer>>();
	private static WeakHashMap<SocketChannel, ByteBuffer> readBufferMap = new WeakHashMap<SocketChannel, ByteBuffer>();

	private static final Header header = new Header();
	private static final ByteBuffer lengthBuf = ByteBuffer.allocate(4);
	
	private static InetAddress broadcastAddr;
	
	public static InetAddress getBroadcastAddr() {
		if(broadcastAddr == null) {
			try {
				broadcastAddr = InetAddress.getByAddress(DEFAULT_BROADCAST_ADDR);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		
		return broadcastAddr;
	}
	
	public static void setBroadcastAddr(InetAddress broadcastAddr) {
		PacketIO.broadcastAddr = broadcastAddr;
	}

	private static Header parseHeader(ByteBuffer buf) throws Exception {
		header.unmarshalling(buf);
		
		return header;
	}
	
	private static Data parseData(ByteBuffer buf, Header header) throws Exception {
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

	private static int read(SocketChannel channel, ByteBuffer buffer) throws IOException {
		int nread = channel.read(buffer);
		if(0 > nread)
			throw new IOException("Peer closed.");
		return nread;
	}

	public static Packet receivePacket(SocketChannel channel) throws Exception {

		ByteBuffer packetBuf = readBufferMap.get(channel);
		if(packetBuf == null) {
			packetBuf = ByteBuffer.allocate(MAX_PACKET_SIZE);
			readBufferMap.put(channel, packetBuf);
		}
		
		if (packetBuf.position() == 0) {
			PacketIO.read(channel, lengthBuf);
			lengthBuf.flip();
			long length = Util.readU32(lengthBuf);
			lengthBuf.flip();
			packetBuf.limit((int)length);
			packetBuf.put(lengthBuf);
			
			lengthBuf.clear();
		}
		
		PacketIO.read(channel, packetBuf);

		if (packetBuf.hasRemaining()) {
			return null;
		}
							
		packetBuf.flip();
		
		Header header = parseHeader(packetBuf);
		Data data = parseData(packetBuf, header);
		
		packetBuf.clear();

		return new Packet(header, data);
	}
	
	private static ByteBuffer buffer = ByteBuffer.allocate(12);

	protected interface PacketReceivedListener {
		void onReceive(DatagramChannel channel, Header header, Data data, InetSocketAddress addr) throws Exception;
	}
	
	public static void receivePacket(DatagramChannel channel, PacketReceivedListener listener) throws Exception {
		buffer.clear();
		InetSocketAddress addr = (InetSocketAddress)channel.receive(buffer);
		buffer.flip();
		
		Header header = parseHeader(buffer);
		Data data = parseData(buffer, header);
		
		listener.onReceive(channel, header, data, addr);
	}

	public static void sendPacket(SocketChannel channel, Packet packet, boolean discardable) throws IOException {
		
		Queue<QueuedBuffer> queue = sendBufferQMap.get(channel);			
		if (queue == null) {
			queue = new ConcurrentLinkedQueue<QueuedBuffer>();
			sendBufferQMap.put(channel, queue);
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
		
		Queue<QueuedBuffer> queue = sendBufferQMap.get(channel);
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
