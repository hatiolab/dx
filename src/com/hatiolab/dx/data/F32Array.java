package com.hatiolab.dx.data;

import java.io.IOException;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class F32Array extends Data {
	
	private long len;
	private int[] buf;
	
	public F32Array() {
		len = 0;
	}
	
	public F32Array(int[] buf) {
		this.len = buf.length;
		this.buf = buf;
	}
	
	public F32Array(int[] buf, int len) {
		this.len = len;
		this.buf = buf;
	}
	
	public void setU16s(int[] data) {
		this.len = data.length;
		this.buf = data;
	}
	
	public int[] getU16s() {
		return buf;
	}
	
	@Override
	public int unmarshalling(byte[] buf, int offset) throws IOException {
		
		if(offset + 4 > buf.length)
			throw new IOException("OutOfBound");
		
		this.len = Util.readU32(buf, offset);
		
		if(offset + 4 + len * 4 > buf.length)
			throw new IOException("OutOfBound");
		
		this.buf = new int[(int)len];
		
		for(int i = 0;i < this.len;i++)
			Util.readS32(buf, offset + 4 + i * 4);

		return getByteLength();
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {

		if(offset + 4 + len > buf.length)
			throw new IOException("OutOfBound");

		Util.writeU32(len, buf, offset);
		
		for(int i = 0;i < this.len;i++)
			Util.writeS32(this.buf[i], buf, offset + 4 + i * 4);
		
		return getByteLength();
	}

	@Override
	public int getByteLength() {
		return (int)(4 + len * 4);
	}

	@Override
	public int getDataType() {
		return TYPE_F32_ARRAY;
	}
}
