package com.hatiolab.dx.data;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class FileInfoArray extends Data implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7173116347434838537L;

	private String	path;

	private FileInfo[] array;
	
	public FileInfoArray() {
	}
	
	public FileInfoArray(String path, FileInfo[] array) {
		this.path = path;
		this.array = array;
	}
	
	public FileInfo[] getArray() {
		return array;
	}

	public void setArray(FileInfo[] array) {
		this.array = array;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public int unmarshalling(byte[] buf, int offset) throws IOException {
		
		if(offset + 4 + Data.PATH_MAX_SIZE > buf.length)
			throw new IOException("OutOfBound");
		
		long len = Util.readU32(buf, offset);
		path = Util.readString(buf, offset + 4, Data.PATH_MAX_SIZE);
		
		array = new FileInfo[(int)len];
		
		if(offset + 4 + Data.PATH_MAX_SIZE + len * (Data.PATH_MAX_SIZE + 8) > buf.length)
			throw new IOException("OutOfBound");
		
		for(int i = 0;i < len;i++) {
			array[i] = new FileInfo();
			array[i].unmarshalling(buf, offset + 4 + Data.PATH_MAX_SIZE + i * (Data.PATH_MAX_SIZE + 8));
		}

		return getByteLength();
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {

		if(offset + 4 + Data.PATH_MAX_SIZE + array.length * (Data.PATH_MAX_SIZE + 8) > buf.length)
			throw new IOException("OutOfBound");

		Util.writeU32(array.length, buf, offset);
		Util.writeString(path, buf, offset + 4, Data.PATH_MAX_SIZE);
		
		for(int i = 0;i < array.length;i++)
			array[i].marshalling(buf, offset + 4 + i * (Data.PATH_MAX_SIZE + 8));
		
		return getByteLength();
	}
	
	@Override
	public void unmarshalling(ByteBuffer buf) throws IOException {
		
		if(4 + Data.PATH_MAX_SIZE > buf.remaining())
			throw new IOException("OutOfBound");
		
		long len = Util.readU32(buf);
		path = Util.readString(buf, Data.PATH_MAX_SIZE);
		
		array = new FileInfo[(int)len];
		
		if(4 + len * (Data.PATH_MAX_SIZE + 8) > buf.remaining())
			throw new IOException("OutOfBound");
		
		for(int i = 0;i < len;i++) {
			array[i] = new FileInfo();
			array[i].unmarshalling(buf);
		}
	}

	@Override
	public void marshalling(ByteBuffer buf) throws IOException {

		if(4 + Data.PATH_MAX_SIZE + array.length * (Data.PATH_MAX_SIZE + 8) > buf.remaining())
			throw new IOException("OutOfBound");

		Util.writeU32(array.length, buf);
		Util.writeString(path, buf, Data.PATH_MAX_SIZE);
		
		for(int i = 0;i < array.length;i++)
			array[i].marshalling(buf);
	}

	@Override
	public int getByteLength() {
		if(array != null)
			return (int)(4 + Data.PATH_MAX_SIZE + array.length * (Data.PATH_MAX_SIZE + 8));
		return 4;
	}

	@Override
	public int getDataType() {
		return TYPE_FILEINFO_ARRAY;
	}
}
