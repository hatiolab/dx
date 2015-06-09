package com.hatiolab.dx.net;

import java.io.IOException;

public interface Marshallable {
	public int unmarshalling(byte[] buf, int offset) throws IOException;
	public int marshalling(byte[] buf, int offset) throws IOException;
	public int getByteLength();
}
