package com.hatiolab.dx.data;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class MovieGetInfo extends Data {
	String path;
	
	public MovieGetInfo(String path) {
		this.path = path;
	}
	
	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {
		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");

		Util.writeString(this.path, buf, offset, Data.PATH_MAX_SIZE);

		return getByteLength();
	}
	
	@Override
	public void marshalling(ByteBuffer buf) throws IOException {
		if(getByteLength() > buf.remaining())
			throw new IOException("OutOfBound");

		Util.writeString(this.path, buf, Data.PATH_MAX_SIZE);
	}
	
	@Override
	public int getByteLength() {
		return Data.PATH_MAX_SIZE;
	}
}
