package com.hatiolab.dx.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

	public static final int readS32(byte[] buf, int offset) {
		return (0x00FF & buf[offset + 3])
				| ((0x00FF & buf[offset + 2]) << 8)
				| ((0x00FF & buf[offset + 1]) << 16)
				| ((0x00FF & buf[offset + 0]) << 24);
	}

	public static final int readU16(byte[] buf, int offset) {
		return (0x00FF & buf[offset + 1])
				| ((0x00FF & buf[offset + 0]) << 8);
	}
	
	public static final short readS16(byte[] buf, int offset) {
		return (short)((0x00FF & buf[offset + 1])
				| ((0x00FF & buf[offset + 0]) << 8));
	}

	public static final short readU8(byte[] buf, int offset) {
		return (short)(0x00FF & buf[offset + 0]);
	}
	
	public static final byte readS8(byte[] buf, int offset) {
		return buf[offset];
	}

	public static final void writeU32(long value, byte[] buf, int offset) {
		buf[offset + 3] = (byte)(0x00FFL & value);
		buf[offset + 2] = (byte)(0x00FFL & (value >> 8));
		buf[offset + 1] = (byte)(0x00FFL & (value >> 16));
		buf[offset + 0] = (byte)(0x00FFL & (value >> 24));
	}
	
	public static final void writeS32(int value, byte[] buf, int offset) {
		buf[offset + 3] = (byte)(0x00FF & value);
		buf[offset + 2] = (byte)(0x00FF & (value >> 8));
		buf[offset + 1] = (byte)(0x00FF & (value >> 16));
		buf[offset + 0] = (byte)(0x00FF & (value >> 24));
	}
	
	public static final void writeU16(int value, byte[] buf, int offset) {
		buf[offset + 1] = (byte)(0x00FFL & value);
		buf[offset + 0] = (byte)(0x00FFL & (value >> 8));
	}
	
	public static final void writeS16(short value, byte[] buf, int offset) {
		buf[offset + 1] = (byte)(0x00FF & value);
		buf[offset + 0] = (byte)(0x00FF & (value >> 8));
	}
	
	public static final void writeU8(short value, byte[] buf, int offset) {
		buf[offset] = (byte)(0x00FF & value);
	}
	
	public static final void writeS8(byte value, byte[] buf, int offset) {
		buf[offset] = value;
	}
	
	public static final float readF32(byte[] buf, int offset) {
		int intBits = (0x00FF & buf[offset + 3])
				| ((0x00FF & buf[offset + 2]) << 8)
				| ((0x00FF & buf[offset + 1]) << 16)
				| ((0x00FF & buf[offset + 0]) << 24);
		
		return Float.intBitsToFloat(intBits);
	}

	public static final void writeF32(float value, byte[] buf, int offset) {
		int intBits = Float.floatToIntBits(value); 
				
		buf[offset + 3] = (byte)(0x00FF & intBits);
		buf[offset + 2] = (byte)(0x00FF & (intBits >> 8));
		buf[offset + 1] = (byte)(0x00FF & (intBits >> 16));
		buf[offset + 0] = (byte)(0x00FF & (intBits >> 24));
	}
	
	public static final String readString(byte[] buf, int offset, int size) throws IOException {
		int i = 0;
		while(i <= size) {
			if(buf[i + offset] == (byte)0) 
				break;
			i++;
		}
		return new String(buf, offset, i >= 0 ? i : size, "UTF-8");
	}

	public static final void writeString(String data, byte[] buf, int offset, int size) throws IOException {
		Arrays.fill(buf, offset, offset + size, (byte)0);
		byte[] bytes = data.getBytes("UTF-8");
		System.arraycopy(bytes, 0, buf, offset, bytes.length > size ? size : bytes.length);
	}
}
