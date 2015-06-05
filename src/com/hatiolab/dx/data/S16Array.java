package com.hatiolab.dx.data;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class S16Array extends Data {
	
	private long len;
	private short[] buf;
	
	public S16Array() {
		len = 0;
	}
	
	public S16Array(short[] buf) {
		this.len = buf.length;
		this.buf = buf;
	}
	
	public S16Array(short[] buf, int len) {
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
		
		if(offset + 4 + len * 2 > buf.length)
			throw new Exception("OutOfBound");
		
		this.buf = new short[(int)len];
		
		for(int i = 0;i < this.len;i++)
			Util.readS16(buf, offset + 4 + i * 2);

		return getByteLength();
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws Exception {

		if(offset + 4 + len * 2 > buf.length)
			throw new Exception("OutOfBound");

		Util.writeU32(len, buf, offset);
		
		for(int i = 0;i < this.len;i++)
			Util.writeS16(this.buf[i], buf, offset + 4 + i * 2);
		
		return getByteLength();
	}

	@Override
	public int getByteLength() {
		return (int)(4 + len * 2);
	}

	@Override
	public int getDataType() {
		return TYPE_S16_ARRAY;
	}
}
