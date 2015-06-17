package com.hatiolab.dx.net;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface Marshallable {
	public int unmarshalling(byte[] buf, int offset) throws IOException;
	public int marshalling(byte[] buf, int offset) throws IOException;
	public void unmarshalling(ByteBuffer buf) throws IOException;
	public void marshalling(ByteBuffer buf) throws IOException;
	public int getByteLength();
}
