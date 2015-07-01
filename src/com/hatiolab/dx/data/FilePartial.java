package com.hatiolab.dx.data;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class FilePartial extends Data {
	String path;
	int totalLen;
	int partialLen;
	int begin;
	int end;
	byte[] content;
	
	public FilePartial() {
	}
	
	public FilePartial(String path, int total_len, int partial_len, int begin, int end, byte[] content) {
		this.path = path;
		this.totalLen = total_len;
		this.partialLen = partial_len;
		this.begin = begin;
		this.end = end;
		this.content = content;
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

	public int getTotalLen() {
		return totalLen;
	}

	public void setTotalLen(int totalLen) {
		this.totalLen = totalLen;
	}

	public int getPartialLen() {
		return partialLen;
	}

	public void setPartialLen(int partialLen) {
		this.partialLen = partialLen;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	@Override
	public int unmarshalling(byte[] buf, int offset) throws IOException {
		
		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");
		
		this.totalLen = (int)Util.readU32(buf, offset);
		this.partialLen = (int)Util.readU32(buf, offset + 4);
		this.begin = (int)Util.readU32(buf, offset + 8);
		this.end = (int)Util.readU32(buf, offset + 12);
		this.path = Util.readString(buf, offset + 16, Data.PATH_MAX_SIZE);
		
		int pos = offset + 16 + Data.PATH_MAX_SIZE;
		this.content = new byte[this.partialLen];
		System.arraycopy(buf, pos, this.content, 0, this.partialLen);

		return getByteLength();
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {

		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");

		Util.writeU32(this.totalLen, buf, offset);
		Util.writeU32(this.partialLen, buf, offset + 4);
		Util.writeU32(this.begin, buf, offset + 8);
		Util.writeU32(this.end, buf, offset + 12);
		Util.writeString(this.path, buf, offset + 16, Data.PATH_MAX_SIZE);
		
		int pos = offset + 16 + Data.PATH_MAX_SIZE;
		System.arraycopy(this.content, 0, buf, pos, this.partialLen);

		return getByteLength();
	}
	
	@Override
	public void unmarshalling(ByteBuffer buf) throws IOException {
		
		if(getByteLength() > buf.remaining())
			throw new IOException("OutOfBound");
		
		this.totalLen = (int)Util.readU32(buf);
		this.partialLen = (int)Util.readU32(buf);
		this.begin = (int)Util.readU32(buf);
		this.end = (int)Util.readU32(buf);
		this.path = Util.readString(buf, Data.PATH_MAX_SIZE);
		
		this.content = new byte[this.partialLen];
		buf.get(this.content);
	}

	@Override
	public void marshalling(ByteBuffer buf) throws IOException {

		if(getByteLength() > buf.remaining())
			throw new IOException("OutOfBound");

		Util.writeU32(this.totalLen, buf);
		Util.writeU32(this.partialLen, buf);
		Util.writeU32(this.begin, buf);
		Util.writeU32(this.end, buf);
		Util.writeString(this.path, buf, Data.PATH_MAX_SIZE);
		
		buf.put(this.content);
	}

	@Override
	public int getByteLength() {
		return Data.PATH_MAX_SIZE + 32 + partialLen;
	}

	@Override
	public int getDataType() {
		return TYPE_FILE_PARTIAL;
	}
}
