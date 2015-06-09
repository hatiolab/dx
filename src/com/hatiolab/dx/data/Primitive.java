package com.hatiolab.dx.data;

import java.io.IOException;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class Primitive extends Data {
	private byte[] buf = new byte[4];
	
	public Primitive() {
	}
	
	public long getU32() {
		return Util.readU32(buf, 0);
	}
	
	public void setU32(long value) {
		Util.writeU32(value, buf, 0);
	}
	
	public int getS32() {
		return Util.readS32(buf, 0);
	}
	
	public void setS32(int value) {
		Util.writeS32(value, buf, 0);
	}
	
	public int getU16() {
		return Util.readU16(buf, 0);
	}
	
	public void setU16(int value) {
		Util.writeU16(value, buf, 0);
	}
	
	public short getS16() {
		return Util.readS16(buf, 0);
	}
	
	public void setS16(short value) {
		Util.writeS16(value, buf, 0);
	}
	
	public short getU8() {
		return Util.readU8(buf, 0);
	}
	
	public void setU8(short value) {
		Util.writeU8(value, buf, 0);
	}
	
	public byte getS8() {
		return Util.readS8(buf, 0);
	}
	
	public void setS8(byte value) {
		Util.writeS8(value, buf, 0);
	}

	public byte[] getBytes() {
		return buf;
	}
	
	public void setBytes(byte[] value) {
		for(int i = 0;i < 4;i++)
			this.buf[i] = value[i];
	}
	
	public float getF32() {
		return Util.readF32(buf, 0);
	}
	
	public void setF32(float value) {
		Util.writeF32(value, buf, 0);
	}

	@Override
	public int unmarshalling(byte[] buf, int offset) throws IOException {
		
		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");
		
		System.arraycopy(buf, offset, this.buf, 0, 4);

		return getByteLength();
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {

		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");

		System.arraycopy(this.buf, 0, buf, offset, 4);
		
		return getByteLength();
	}

	@Override
	public int getByteLength() {
		return 4;
	}

	@Override
	public int getDataType() {
		return TYPE_PRIMITIVE;
	}
}
