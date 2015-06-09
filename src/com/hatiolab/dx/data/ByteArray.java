package com.hatiolab.dx.data;

import java.io.IOException;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class ByteArray extends Data {
	
	private long len;
	private byte[] buf;
	
	public ByteArray() {
		len = 0;
	}
	
	public ByteArray(byte[] buf) {
		this.len = buf.length;
		this.buf = buf;
	}
	
	public ByteArray(byte[] buf, int len) {
		this.len = len;
		this.buf = buf;
	}
	
	public void setBytes(byte[] data) {
		this.len = data.length;
		this.buf = data;
	}
	
	public byte[] getBytes() {
		return buf;
	}
	
	@Override
	public int unmarshalling(byte[] buf, int offset) throws IOException {
		
		if(offset + 4 > buf.length)
			throw new IOException("OutOfBound");
		
		this.len = Util.readU32(buf, offset);
		
		if(offset + 4 + len > buf.length)
			throw new IOException("OutOfBound");
		
		this.buf = new byte[(int)len];
		
		System.arraycopy(buf, offset + 4, this.buf, 0, (int)len);

		return getByteLength();
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {

		if(offset + 4 + len > buf.length)
			throw new IOException("OutOfBound");

		Util.writeU32(len, buf, offset);
		
		System.arraycopy(this.buf, 0, buf, offset + 4, (int)len);
		
		return getByteLength();
	}

	@Override
	public int getByteLength() {
		return (int)(4 + len);
	}

	@Override
	public int getDataType() {
		return TYPE_S8_ARRAY;
	}
}
