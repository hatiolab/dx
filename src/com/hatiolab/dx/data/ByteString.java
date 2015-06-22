package com.hatiolab.dx.data;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class ByteString extends Data {
	
	private long len;
	private String data;
	
	public ByteString() {
	}
	
	public ByteString(String data) {
		try {
			this.len = data.getBytes("UTF-8").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		this.data = data;
	}
		
	public ByteString(String data, int len) {
		this.len = len;
		this.data = data;
	}
		
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public int unmarshalling(byte[] buf, int offset) throws IOException {
		
		if(offset + 4 > buf.length)
			throw new IOException("OutOfBound");

		len = Util.readU32(buf, offset);
		
		if(offset + 4 + len > buf.length)
			throw new IOException("OutOfBound");
		
		this.data = Util.readString(buf, offset + 4, (int)len);

		return getByteLength();
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {

		if(offset + 4 + len > buf.length)
			throw new IOException("OutOfBound");

		Util.writeU32(len, buf, offset);
		
		Util.writeString(data, buf, offset + 4, (int)len);
		
		return getByteLength();
	}
	
	@Override
	public void unmarshalling(ByteBuffer buf) throws IOException {
		
		if(4 > buf.remaining())
			throw new IOException("OutOfBound");

		this.len = (int)Util.readU32(buf);
		
		if(this.len > buf.remaining())
			throw new IOException("OutOfBound");
		Util.writeString(data, buf, (int)this.len);
	}
	
	@Override
	public void marshalling(ByteBuffer buf) throws IOException {

		if(getByteLength() > buf.remaining())
			throw new IOException("OutOfBound");

		Util.writeU32(len, buf);
		
		Util.writeString(data, buf, (int)this.len);
	}
	
	@Override
	public int getByteLength() {
		return (int)(4 + len);
	}

	@Override
	public int getDataType() {
		return TYPE_STRING;
	}
}
