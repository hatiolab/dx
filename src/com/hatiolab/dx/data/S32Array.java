package com.hatiolab.dx.data;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class S32Array extends Data {
	
	private long len;
	private float[] buf;
	
	public S32Array() {
		len = 0;
	}
	
	public S32Array(float[] buf) {
		this.len = buf.length;
		this.buf = buf;
	}
	
	public S32Array(float[] buf, int len) {
		this.len = len;
		this.buf = buf;
	}
	
	public void setU16s(float[] data) {
		this.len = data.length;
		this.buf = data;
	}
	
	public float[] getU16s() {
		return buf;
	}
	
	@Override
	public int unmarshalling(byte[] buf, int offset) throws Exception {
		
		if(offset + 4 > buf.length)
			throw new Exception("OutOfBound");
		
		this.len = Util.readU32(buf, offset);
		
		if(offset + 4 + len * 4 > buf.length)
			throw new Exception("OutOfBound");
		
		this.buf = new float[(int)len];
		
		for(int i = 0;i < this.len;i++)
			Util.readF32(buf, offset + 4 + i * 4);

		return getByteLength();
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws Exception {

		if(offset + 4 + len * 4 > buf.length)
			throw new Exception("OutOfBound");

		Util.writeU32(len, buf, offset);
		
		for(int i = 0;i < this.len;i++)
			Util.writeF32(this.buf[i], buf, offset + 4 + i * 4);
		
		return getByteLength();
	}

	@Override
	public int getByteLength() {
		return (int)(4 + len * 4);
	}

	@Override
	public int getDataType() {
		return TYPE_S32_ARRAY;
	}
}
