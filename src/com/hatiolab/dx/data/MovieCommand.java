package com.hatiolab.dx.data;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class MovieCommand extends Data {
	
	String path;
	int startFrame;
	int stopFrame;
	int framesPerSec;
	
	public MovieCommand(String path, int startFrame, int stopFrame, int framesPerSec) {
		this.path = path;
		this.startFrame = startFrame;
		this.stopFrame = stopFrame;
		this.framesPerSec = framesPerSec;
	}
	
	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {
		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");

		Util.writeString(this.path, buf, offset, Data.PATH_MAX_SIZE);
		Util.writeU32(this.startFrame, buf, offset + Data.PATH_MAX_SIZE);
		Util.writeU32(this.stopFrame, buf, offset + Data.PATH_MAX_SIZE + 4);
		Util.writeU16(this.framesPerSec, buf, offset + Data.PATH_MAX_SIZE + 4 + 4);

		return getByteLength();
	}
	
	@Override
	public void marshalling(ByteBuffer buf) throws IOException {
		if(getByteLength() > buf.remaining())
			throw new IOException("OutOfBound");

		Util.writeString(this.path, buf, Data.PATH_MAX_SIZE);
		Util.writeU32(this.startFrame, buf);
		Util.writeU32(this.stopFrame, buf);
		Util.writeU16(this.framesPerSec, buf);
	}
	
	@Override
	public int getByteLength() {
		return Data.PATH_MAX_SIZE + 10;
	}
}
