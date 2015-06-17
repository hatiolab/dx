package com.hatiolab.dx.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.hatiolab.dx.net.Marshallable;
import com.hatiolab.dx.net.Util;

public class Header implements Marshallable {
	private long len;
	private int type;
	private int code;
	private int dataType;
//	private byte reserved2;
	
	public Header() {
	}
	
	public Header(int type, int code, int dataType) {
		this.type = type;
		this.code = code;
		this.dataType = dataType;
	}

	public long getLen() {
		return len;
	}
	
	public void setLen(long len) {
		this.len = len;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public int getCode() {
		return code;
	}

	public void setCode(byte code) {
		this.code = code;
	}

	@Override
	public int unmarshalling(byte[] buf, int offset) throws IOException {
		
		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");
		
		this.len = Util.readU32(buf, offset);
		this.type = Util.readU8(buf, offset + 4);
		this.code = Util.readU8(buf, offset + 5);
		this.dataType = Util.readU8(buf, offset + 6);

		return getByteLength();
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {

		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");

		Util.writeU32(this.len, buf, offset);
		Util.writeU8((short)this.type, buf, offset + 4);
		Util.writeU8((short)this.code, buf, offset + 5);
		Util.writeU8((short)this.dataType, buf, offset + 6);
		
		return getByteLength();
	}

	@Override
	public void unmarshalling(ByteBuffer buf) throws IOException {
		if(getByteLength() > buf.remaining())
			throw new IOException("OutOfBound");
		
		this.len = Util.readU32(buf);
		this.type = Util.readU8(buf);
		this.code = Util.readU8(buf);
		this.dataType = Util.readU8(buf);
		int dummy = Util.readU8(buf);
	}

	@Override
	public void marshalling(ByteBuffer buf) throws IOException {
		if(getByteLength() > buf.remaining())
			throw new IOException("OutOfBound");

		Util.writeU32(this.len, buf);
		Util.writeU8((short)this.type, buf);
		Util.writeU8((short)this.code, buf);
		Util.writeU8((short)this.dataType, buf);
		Util.writeU8(0, buf);
	}

	@Override
	public int getByteLength() {
		return 8;
	}
}
