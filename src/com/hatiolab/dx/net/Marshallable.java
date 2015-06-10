package com.hatiolab.dx.net;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface Marshallable {
	public int unmarshalling(byte[] buf, int offset) throws IOException;
	public int marshalling(byte[] buf, int offset) throws IOException;
	public int unmarshalling(ByteBuffer buf, int offset) throws IOException;
	public int marshalling(ByteBuffer buf, int offset) throws IOException;
	public int getByteLength();
}
