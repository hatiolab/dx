package com.hatiolab.dx.data;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class U8Array extends Data {
	
	private long len;
	private short[] buf;
	
	public U8Array() {
		len = 0;
	}
	
	public U8Array(short[] buf) {
		this.len = buf.length;
		this.buf = buf;
	}
	
	public U8Array(short[] buf, int len) {
		this.len = len;
		this.buf = buf;
	}
	
	public void setU16s(short[] data) {
		this.len = data.length;
		this.buf = data;
	}
	
	public short[] getU16s() {
		return buf;
	}
	
	@Override
	public int unmarshalling(byte[] buf, int offset) throws Exception {
		
		if(offset + 4 > buf.length)
			throw new Exception("OutOfBound");
		
		this.len = Util.readU32(buf, offset);
		
		if(offset + 4 + len > buf.length)
			throw new Exception("OutOfBound");
		
		this.buf = new short[(int)len];
		
		for(int i = 0;i < this.len;i++)
			Util.readU8(buf, offset + 4 + i);

		return getByteLength();
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws Exception {

		if(offset + 4 + len > buf.length)
			throw new Exception("OutOfBound");

		Util.writeU32(len, buf, offset);
		
		for(int i = 0;i < this.len;i++)
			Util.writeU8(this.buf[i], buf, offset + 4 + i);
		
		return getByteLength();
	}

	@Override
	public int getByteLength() {
		return (int)(4 + len);
	}

	@Override
	public int getDataType() {
		return TYPE_U8_ARRAY;
	}

}
