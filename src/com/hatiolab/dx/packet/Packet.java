package com.hatiolab.dx.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.hatiolab.dx.net.Marshallable;

public class Packet implements Marshallable {
	private Header header;
	private Data data;
	
	public Packet(int type, int code, Data data) {
		header = new Header(type, code, data == null ? Data.TYPE_NONE : data.getDataType());
		this.data = data;
	}
	
	public Packet(Header header, Data data) {
		this.header = header;
		this.data = data;
	}
	
	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public int unmarshalling(byte[] buf, int offset) throws IOException {
		int len = header.unmarshalling(buf, offset);
		if(data != null)
			len += data.unmarshalling(buf, offset + len);

		return len;
	}

	public int marshalling(byte[] buf, int offset) throws IOException {
		/* encoding 하기 전에 전체 길이를 header에 설정한다. */ 
		header.setLen(getByteLength()); 
				
		int len = header.marshalling(buf, offset);
		if(data != null)
			len += data.marshalling(buf, offset + len);
		
		return len;
	}

	@Override
	public void unmarshalling(ByteBuffer buf) throws IOException {
		header.unmarshalling(buf);
		if(data != null)
			data.unmarshalling(buf);
	}

	@Override
	public void marshalling(ByteBuffer buf) throws IOException {
		header.setLen(getByteLength());
		
		header.marshalling(buf);
		if(data != null)
			data.marshalling(buf);
	}

	public int getByteLength() {
		int len = header.getByteLength();
		if(data != null)
			len += data.getByteLength();
		
		return len;
	}

}
