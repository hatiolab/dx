package com.hatiolab.dx.net;

public interface Marshallable {
	public int unmarshalling(byte[] buf, int offset) throws Exception;
	public int marshalling(byte[] buf, int offset) throws Exception;
	public int getByteLength();
}
