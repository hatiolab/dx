package com.hatiolab.dx.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.hatiolab.dx.net.Marshallable;

public class Data implements Marshallable {
	public static final int PATH_MAX_SIZE = 128;
	public static final int FILE_PARTIAL_MAX_SIZE = 500000;

	/* Data Types */

	public static final int TYPE_NONE =				0;		/* Empty Data */
	public static final int TYPE_PRIMITIVE	=		1;		/* Primitive Data */
	public static final int TYPE_U8_ARRAY	=		2;		/* U8 Array */
	public static final int TYPE_S8_ARRAY	=		3;		/* S8 Array */
	public static final int TYPE_U16_ARRAY	=		4;		/* U16 Array */
	public static final int TYPE_S16_ARRAY	=		5;		/* S16 Array */
	public static final int TYPE_U32_ARRAY	=		6;		/* U32 Array */
	public static final int TYPE_S32_ARRAY	=		7;		/* S32 Array */
	public static final int TYPE_F32_ARRAY	=		8;		/* F32 Array */
	public static final int TYPE_STRING		=		9;		/* F32 Array */

	public static final int TYPE_FILEINFO	=		21;		/* File Info Data */
	public static final int TYPE_FILEINFO_ARRAY	=	22;		/* File Info Array */
	public static final int TYPE_FILE_PARTIAL_QUERY	=	23;		/* File Partial Query */
	public static final int TYPE_FILE_PARTIAL	=	24;		/* File Partial */

	public static final int TYPE_STREAM		=		41;		/* Streaming Data */

	@Override
	public int unmarshalling(byte[] buf, int offset) throws IOException {
		return 0;
	}
	
	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {
		return 0;
	}

	@Override
	public void unmarshalling(ByteBuffer buf) throws IOException {
		
	}

	@Override
	public void marshalling(ByteBuffer buf) throws IOException {
		
	}

	public int getByteLength() {
		return 0;
	}
	
	public int getDataType() {
		return TYPE_NONE;
	}

}
