package com.hatiolab.dx.data;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class FileInfo extends Data implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7128867223704226765L;
	int size;
	int mtime;	// Last Modified Time
	String path;
	
	public FileInfo() {
	}
	
	public FileInfo(int size, int mtime, String path) {
		this.size = size;
		this.mtime = mtime;
		this.path = path;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getMtime() {
		return mtime;
	}

	public void setMtime(int mtime) {
		this.mtime = mtime;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public int unmarshalling(byte[] buf, int offset) throws IOException {
		
		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");
		
		this.size = (int)Util.readU32(buf, offset);
		this.mtime = (int)Util.readU32(buf, offset + 4);
		this.path = Util.readString(buf, offset + 8, Data.PATH_MAX_SIZE);

		return getByteLength();
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {

		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");

		Util.writeU32(this.size, buf, offset);
		Util.writeU32(this.mtime, buf, offset + 4);
		Util.writeString(this.path, buf, offset + 8, Data.PATH_MAX_SIZE);
		
		return getByteLength();
	}
	
	@Override
	public void unmarshalling(ByteBuffer buf) throws IOException {
		
		if(getByteLength() > buf.remaining())
			throw new IOException("OutOfBound");
		
		this.size = (int)Util.readU32(buf);
		this.mtime = (int)Util.readU32(buf);
		this.path = Util.readString(buf, Data.PATH_MAX_SIZE);
	}

	@Override
	public void marshalling(ByteBuffer buf) throws IOException {

		if(getByteLength() > buf.remaining())
			throw new IOException("OutOfBound");

		Util.writeU32(this.size, buf);
		Util.writeU32(this.mtime, buf);
		Util.writeString(this.path, buf, Data.PATH_MAX_SIZE);
	}

	@Override
	public int getByteLength() {
		return Data.PATH_MAX_SIZE + 8;
	}

	@Override
	public int getDataType() {
		return TYPE_FILEINFO;
	}

}
