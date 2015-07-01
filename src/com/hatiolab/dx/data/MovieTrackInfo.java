package com.hatiolab.dx.data;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class MovieTrackInfo extends Data{
	String id;
	String type;
	
	public MovieTrackInfo() {
		// do nothing
	}
	
	public MovieTrackInfo(String id, String type) {
		this.id = id;
		this.type = type;
	}
	
	@Override
	public int unmarshalling(byte[] buf, int offset) throws IOException {
		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");
		
		this.id = Util.readString(buf, offset, TRACK_ID_SIZE);
		this.type = Util.readString(buf, offset, TRACK_TYPE_SIZE);
		
		return getByteLength();
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {

		// TODO
		return getByteLength();
	}
	
	@Override
	public void unmarshalling(ByteBuffer buf) throws IOException {
		
		if(getByteLength() > buf.remaining())
			throw new IOException("OutOfBound");
		
		this.id = Util.readString(buf, TRACK_ID_SIZE);
		this.type = Util.readString(buf, TRACK_TYPE_SIZE);
	}

	@Override
	public void marshalling(ByteBuffer buf) throws IOException {
		// TODO
	}

	@Override
	public int getByteLength() {
		return TRACK_ID_SIZE + TRACK_TYPE_SIZE;
	}
}
