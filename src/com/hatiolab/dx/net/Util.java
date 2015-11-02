package com.hatiolab.dx.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Util {
	
	public static final int read(InputStream is, byte[] buf, int len) throws IOException {
		int nread = 0;
		int tread = 0;
		
		while(len - tread > 0) {
			nread = is.read(buf, tread, len - tread);
			if(nread == 0)
				throw new IOException("Hung Up");
			else if(nread == -1)
				throw new IOException("Read Error");
			tread += nread;
		}
		
		return tread;
	}

	public synchronized static final void write(OutputStream os, byte[] buf, int len) throws IOException {
		os.write(buf, 0, len);
	}
	
	public static final long readU32(byte[] buf, int offset) {
		return (0x00FFL & buf[offset + 3])
				| ((0x00FFL & buf[offset + 2]) << 8)
				| ((0x00FFL & buf[offset + 1]) << 16)
				| ((0x00FFL & buf[offset + 0]) << 24);
	}

	public static final long readU32(ByteBuffer buf) {
		byte[] tmpbuf = new byte[4];
		buf.get(tmpbuf);
		
		return (0x00FFL & tmpbuf[3])
				| ((0x00FFL & tmpbuf[2]) << 8)
				| ((0x00FFL & tmpbuf[1]) << 16)
				| ((0x00FFL & tmpbuf[0]) << 24);
	}
	
	public static final long readU64(ByteBuffer buf) {
		byte[] tmpbuf = new byte[8];
		buf.get(tmpbuf);
		
		return (0x00FFL & tmpbuf[7])
				| ((0x00FFL & tmpbuf[6]) << 8)
				| ((0x00FFL & tmpbuf[5]) << 16)
				| ((0x00FFL & tmpbuf[4]) << 24)
				| ((0x00FFL & tmpbuf[3]) << 32)
				| ((0x00FFL & tmpbuf[2]) << 40)
				| ((0x00FFL & tmpbuf[1]) << 48)
				| ((0x00FFL & tmpbuf[0]) << 56);
	}

	public static final int readS32(byte[] buf, int offset) {
		return (0x00FF & buf[offset + 3])
				| ((0x00FF & buf[offset + 2]) << 8)
				| ((0x00FF & buf[offset + 1]) << 16)
				| ((0x00FF & buf[offset + 0]) << 24);
	}
	
	public static final long readS32(ByteBuffer buf) {
		byte[] tmpbuf = new byte[4];
		buf.get(tmpbuf);
		
		return (0x00FF & tmpbuf[3])
				| ((0x00FF & tmpbuf[2]) << 8)
				| ((0x00FF & tmpbuf[1]) << 16)
				| ((0x00FF & tmpbuf[0]) << 24);
	}
	
	public static final long readS64(ByteBuffer buf) {
		byte[] tmpbuf = new byte[8];
		buf.get(tmpbuf);
		
		return (0x00FFL & tmpbuf[7])
				| ((0x00FFL & tmpbuf[6]) << 8)
				| ((0x00FFL & tmpbuf[5]) << 16)
				| ((0x00FFL & tmpbuf[4]) << 24)
				| ((0x00FFL & tmpbuf[3]) << 32)
				| ((0x00FFL & tmpbuf[2]) << 40)
				| ((0x00FFL & tmpbuf[1]) << 48)
				| ((0x00FFL & tmpbuf[0]) << 56);
	}

	public static final int readU16(byte[] buf, int offset) {
		return (0x00FF & buf[offset + 1])
				| ((0x00FF & buf[offset + 0]) << 8);
	}
	
	public static final long readU16(ByteBuffer buf) {
		byte[] tmpbuf = new byte[2];
		buf.get(tmpbuf);
		
		return (0x00FFL & tmpbuf[1])
				| ((0x00FFL & tmpbuf[0]) << 8);
	}
	
	public static final short readS16(byte[] buf, int offset) {
		return (short)((0x00FF & buf[offset + 1])
				| ((0x00FF & buf[offset + 0]) << 8));
	}

	public static final short readS16(ByteBuffer buf) {
		byte[] tmpbuf = new byte[2];
		buf.get(tmpbuf);
		
		return (short)((0x00FF & tmpbuf[1])
				| ((0x00FF & tmpbuf[0]) << 8));
	}

	public static final short readU8(byte[] buf, int offset) {
		return (short)(0x00FF & buf[offset + 0]);
	}
	
	public static final short readU8(ByteBuffer buf) {
		return (short)(0x00FF & buf.get());
	}
	
	public static final byte readS8(byte[] buf, int offset) {
		return buf[offset];
	}

	public static final short readS8(ByteBuffer buf) {
		return buf.get();
	}
	
	public static final void writeU32(long value, byte[] buf, int offset) {
		buf[offset + 3] = (byte)(0x00FFL & value);
		buf[offset + 2] = (byte)(0x00FFL & (value >> 8));
		buf[offset + 1] = (byte)(0x00FFL & (value >> 16));
		buf[offset + 0] = (byte)(0x00FFL & (value >> 24));
	}
	
	public static final void writeU32(long value, ByteBuffer buf) {
		buf.put((byte)(0x00FFL & (value >> 24)));
		buf.put((byte)(0x00FFL & (value >> 16)));
		buf.put((byte)(0x00FFL & (value >> 8)));
		buf.put((byte)(0x00FFL & value));
	}
	
	public static final void writeS32(int value, byte[] buf, int offset) {
		buf[offset + 3] = (byte)(0x00FF & value);
		buf[offset + 2] = (byte)(0x00FF & (value >> 8));
		buf[offset + 1] = (byte)(0x00FF & (value >> 16));
		buf[offset + 0] = (byte)(0x00FF & (value >> 24));
	}
	
	public static final void writeS32(int value, ByteBuffer buf) {
		buf.put((byte)(0x00FF & (value >> 24)));
		buf.put((byte)(0x00FF & (value >> 16)));
		buf.put((byte)(0x00FF & (value >> 8)));
		buf.put((byte)(0x00FF & value));
	}
	
	public static final void writeU16(int value, byte[] buf, int offset) {
		buf[offset + 1] = (byte)(0x00FFL & value);
		buf[offset + 0] = (byte)(0x00FFL & (value >> 8));
	}
	
	public static final void writeU16(long value, ByteBuffer buf) {
		buf.put((byte)(0x00FFL & (value >> 8)));
		buf.put((byte)(0x00FFL & value));
	}
	
	public static final void writeS16(short value, byte[] buf, int offset) {
		buf[offset + 1] = (byte)(0x00FF & value);
		buf[offset + 0] = (byte)(0x00FF & (value >> 8));
	}
	
	public static final void writeS16(long value, ByteBuffer buf) {
		buf.put((byte)(0x00FF & (value >> 8)));
		buf.put((byte)(0x00FF & value));
	}
	
	public static final void writeU8(short value, byte[] buf, int offset) {
		buf[offset] = (byte)(0x00FF & value);
	}
	
	public static final void writeU8(short value, ByteBuffer buf) {
		buf.put((byte)(0x00FF & value));
	}	
	
	public static final void writeS8(byte value, byte[] buf, int offset) {
		buf[offset] = value;
	}
	
	public static final void writeU8(byte value, ByteBuffer buf) {
		buf.put(value);
	}	
	
	public static final float readF32(byte[] buf, int offset) {
		int intBits = (0x00FF & buf[offset + 3])
				| ((0x00FF & buf[offset + 2]) << 8)
				| ((0x00FF & buf[offset + 1]) << 16)
				| ((0x00FF & buf[offset + 0]) << 24);
		
		return Float.intBitsToFloat(intBits);
	}

	public static final float readF32(ByteBuffer buf) {
		byte[] tmpbuf = new byte[4];
		buf.get(tmpbuf);
		
		int intBits =  (0x00FF & tmpbuf[3])
				| ((0x00FF & tmpbuf[2]) << 8)
				| ((0x00FF & tmpbuf[1]) << 16)
				| ((0x00FF & tmpbuf[0]) << 24);

		return Float.intBitsToFloat(intBits);
	}

	public static final void writeF32(float value, byte[] buf, int offset) {
		int intBits = Float.floatToIntBits(value); 
				
		buf[offset + 3] = (byte)(0x00FF & intBits);
		buf[offset + 2] = (byte)(0x00FF & (intBits >> 8));
		buf[offset + 1] = (byte)(0x00FF & (intBits >> 16));
		buf[offset + 0] = (byte)(0x00FF & (intBits >> 24));
	}
	
	public static final void writeF32(long value, ByteBuffer buf) {
		buf.put((byte)(0x00FF & (value >> 24)));
		buf.put((byte)(0x00FF & (value >> 16)));
		buf.put((byte)(0x00FF & (value >> 8)));
		buf.put((byte)(0x00FF & value));
	}
	
	public static final String readString(byte[] buf, int offset, int size) throws IOException {
		int i = 0;
		while(i <= size) {
			if(buf[i + offset] == (byte)0) 
				break;
			i++;
		}
		return new String(buf, offset, i >= 0 ? i : size, "UTF-8").trim();
	}
	
	public static final String readString(ByteBuffer buf, int size) throws IOException {
		byte[] tmpbuf = new byte[size];

		buf.get(tmpbuf, 0, size);
		
		return new String(tmpbuf, "UTF-8").trim();
	}

	public static final String readString(ByteBuffer buf, int size, String charSet) throws IOException {
		byte[] tmpbuf = new byte[size];

		buf.get(tmpbuf, 0, size);
		
		return new String(tmpbuf, charSet);
	}
	
	public static final void writeString(String data, byte[] buf, int offset, int size) throws IOException {
		Arrays.fill(buf, offset, offset + size, (byte)0);
		byte[] bytes = data.getBytes("UTF-8");
		System.arraycopy(bytes, 0, buf, offset, bytes.length > size ? size : bytes.length);
	}
	
	public static final void writeString(String data, ByteBuffer buf, int size) throws IOException {
		byte[] bytes = data.getBytes("UTF-8");

		int sz = size > bytes.length ? bytes.length : size;
		
		buf.put(bytes, 0, sz);
		
		while(sz++ < size)
			buf.put((byte)0x0);
	}
}
