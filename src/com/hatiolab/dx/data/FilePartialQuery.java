package com.hatiolab.dx.data;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class FilePartialQuery extends Data {
	String path;
	int begin;
	int end;
	
	public FilePartialQuery() {
	}
	
	public FilePartialQuery(String path, int begin, int end) {
		this.path = path;
		this.begin = begin;
		this.end = end;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	@Override
	public int unmarshalling(byte[] buf, int offset) throws IOException {
		
		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");
		
		this.begin = (int)Util.readU32(buf, offset);
		this.end = (int)Util.readU32(buf, offset + 4);
		this.path = Util.readString(buf, offset + 8, Data.PATH_MAX_SIZE);

		return getByteLength();
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {

		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");

		Util.writeU32(this.begin, buf, offset);
		Util.writeU32(this.end, buf, offset + 4);
		Util.writeString(this.path, buf, offset + 8, Data.PATH_MAX_SIZE);
		
		return getByteLength();
	}
	
	@Override
	public void unmarshalling(ByteBuffer buf) throws IOException {
		
		if(getByteLength() > buf.remaining())
			throw new IOException("OutOfBound");
		
		this.begin = (int)Util.readU32(buf);
		this.end = (int)Util.readU32(buf);
		this.path = Util.readString(buf, Data.PATH_MAX_SIZE);
	}

	@Override
	public void marshalling(ByteBuffer buf) throws IOException {

		if(getByteLength() > buf.remaining())
			throw new IOException("OutOfBound");

		Util.writeU32(this.begin, buf);
		Util.writeU32(this.end, buf);
		Util.writeString(this.path, buf, Data.PATH_MAX_SIZE);
	}

	@Override
	public int getByteLength() {
		return Data.PATH_MAX_SIZE + 8;
	}

	@Override
	public int getDataType() {
		return TYPE_FILE_PARTIAL_QUERY;
	}
}
