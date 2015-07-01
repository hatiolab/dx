package com.hatiolab.dx.data;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class MovieTrackIndex extends Data {
	String trackId;
	int offset;
	int length;
	int flags;
	int reserved;
	
	public MovieTrackIndex() {
		// do nothing
	}
	
	public MovieTrackIndex(String trackId, int offset, int length, int flags) {
		this.trackId = trackId;
		this.offset = offset;
		this.length = length;
		this.flags = flags;
	}
	
	public String getTrackId() {
		return trackId;
	}
	
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public int getFlags() {
		return flags;
	}
	
	public void setFlags(int flags) {
		this.flags = flags;
	}
	
	@Override
	public int unmarshalling(byte[] buf, int offset) throws IOException {
		if (this.offset + this.getByteLength() > buf.length) {
			throw new IOException("OutOfBound");
		}
		
		this.trackId = Util.readString(buf, offset, TRACK_ID_SIZE);
		this.offset = (int)Util.readU32(buf, offset + TRACK_ID_SIZE);
		this.length = (int)Util.readU32(buf, offset + TRACK_ID_SIZE + 4);
		this.flags = (int)Util.readU8(buf, offset + TRACK_ID_SIZE + 4 + 4);
		Util.readU8(buf, offset + TRACK_ID_SIZE + 4 + 4 + 1);
		Util.readU8(buf, offset + TRACK_ID_SIZE + 4 + 4 + 1 + 1);
		Util.readU8(buf, offset + TRACK_ID_SIZE + 4 + 4 + 1 + 1 + 1);
		
		return getByteLength();
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {
		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");

		Util.writeString(this.trackId, buf, offset, TRACK_ID_SIZE);
		Util.writeU32(this.offset, buf, offset + TRACK_ID_SIZE);
		Util.writeU32(this.length, buf, offset + TRACK_ID_SIZE + 4);
		Util.writeU8((short)this.flags, buf, offset + TRACK_ID_SIZE + 4 + 4);
		
		return getByteLength();
	}
	
	@Override
	public void unmarshalling(ByteBuffer buf) throws IOException {
		if (this.offset + this.getByteLength() > buf.remaining()) {
			throw new IOException("OutOfBound");
		}
		this.trackId = Util.readString(buf, TRACK_ID_SIZE);
		this.offset = (int)Util.readU32(buf);
		this.length = (int)Util.readU32(buf);
		this.flags = (int)Util.readU8(buf);
		Util.readU8(buf); /* reserved field */
		Util.readU8(buf);
		Util.readU8(buf);
	}

	@Override
	public void marshalling(ByteBuffer buf) throws IOException {
		if(offset + getByteLength() > buf.remaining())
			throw new IOException("OutOfBound");

		Util.writeString(this.trackId, buf, TRACK_ID_SIZE);
		Util.writeU32(this.offset, buf);
		Util.writeU32(this.length, buf);
		Util.writeU8((short)this.flags, buf);
		// reserved
	}

	@Override
	public int getByteLength() {
		return this.length;
	}
}
