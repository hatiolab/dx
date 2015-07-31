package com.hatiolab.dx.data;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class SdCardInfo extends Data {
	int statusFlag;
	int total;
	int usage;
	
	public SdCardInfo() {
		// do nothing
	}
	
	public SdCardInfo(int statusFlag, int total, int usage) {
		this.statusFlag = statusFlag;
		this.total = total;
		this.usage = usage;
	}

	public int getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(int statusFlag) {
		this.statusFlag = statusFlag;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getUsage() {
		return usage;
	}

	public void setUsage(int usage) {
		this.usage = usage;
	}
	
	@Override
	public int unmarshalling(byte[] buf, int offset) throws IOException {
		
		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");
		
		this.statusFlag = (int)Util.readU16(buf, offset);
		this.total = (int)Util.readU32(buf, offset + 2);
		this.usage = (int)Util.readU32(buf, offset + 6);
		Util.readU16(buf, offset + 10);
		
		return getByteLength();
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {

		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");

		Util.writeU16(this.statusFlag, buf, offset);
		Util.writeU32(this.total, buf, offset + 2);
		Util.writeU32(this.usage, buf, offset + 6);
		Util.writeU16(this.usage, buf, offset + 10);
		
		return getByteLength();
	}
	
	@Override
	public void unmarshalling(ByteBuffer buf) throws IOException {
		
		if(getByteLength() > buf.remaining())
			throw new IOException("OutOfBound");
		
		this.statusFlag = (short)Util.readU16(buf);
		this.total = (int)Util.readU32(buf);
		this.usage = (int)Util.readU32(buf);
		Util.readU16(buf);
	}

	@Override
	public void marshalling(ByteBuffer buf) throws IOException {

		if(getByteLength() > buf.remaining())
			throw new IOException("OutOfBound");

		Util.writeU16(this.statusFlag, buf);
		Util.writeU32(this.total, buf);
		Util.writeU32(this.usage, buf);
	}

	@Override
	public int getByteLength() {
		return 20;
	}
}
