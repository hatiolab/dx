package com.hatiolab.dx.data;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class Stream extends Data {
	int len;
	int type;
	static byte[] content = new byte[3 * 1024 * 1024];

	public Stream() {
	}
	
	public Stream(int type, byte[] content) {
		this.type = type;
		this.len = content.length;

		System.arraycopy(content, 0, Stream.content, 0, content.length);
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.len = content.length;
		
		System.arraycopy(content, 0, Stream.content, 0, content.length);
	}

	@Override
	public int unmarshalling(byte[] buf, int offset) throws IOException {
		
		if(offset + 8 > buf.length)
			throw new IOException("OutOfBound");

		this.len = (int)Util.readU32(buf, offset);
		this.type = (int)Util.readU16(buf, offset + 4);

		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");
		
		int pos = offset + 8;
		
		System.arraycopy(buf, pos, Stream.content, 0, this.len);

		return getByteLength();
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {

		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");

		Util.writeU32(this.len, buf, offset);
		Util.writeU16(this.type, buf, offset + 4);
		Util.writeU16(0, buf, offset + 6);
		
		int pos = offset + 8;
		
		System.arraycopy(Stream.content, 0, buf, pos, this.len);

		return getByteLength();
	}

	@Override
	public void unmarshalling(ByteBuffer buf) throws IOException {

		if(8 > buf.remaining())
			throw new IOException("OutOfBound");

		this.len = (int)Util.readU32(buf);
		this.type = (int)Util.readU16(buf);
		int dummy = (int)Util.readU16(buf);

		if(this.len > buf.remaining())
			throw new IOException("OutOfBound");
		
		buf.put(Stream.content, 0, this.len);
	}
	
	@Override
	public void marshalling(ByteBuffer buf) throws IOException {

		if(getByteLength() > buf.remaining())
			throw new IOException("OutOfBound");

		Util.writeU32(this.len, buf);
		Util.writeU16(this.type, buf);
		Util.writeU16(0, buf);
		
		buf.put(Stream.content, 0, this.len);
	}
	
	@Override
	public int getByteLength() {
		return 8 + len;
	}

	@Override
	public int getDataType() {
		return TYPE_STREAM;
	}
}
