package com.hatiolab.dx.packet;

public class Type {

	/* Packet Type */

    public final static int DX_PACKET_TYPE_HB				= 0;	/* Heart Beat */
    public final static int DX_PACKET_TYPE_GET_SETTING		= 1;	/* Get Setting */
    public final static int DX_PACKET_TYPE_SET_SETTING		= 2;	/* Set Setting */
    public final static int DX_PACKET_TYPE_GET_STATE		= 3;	/* Get State */
    public final static int DX_PACKET_TYPE_SET_STATE		= 4;	/* Set State */
    public final static int DX_PACKET_TYPE_EVENT			= 5;	/* Event */
    public final static int DX_PACKET_TYPE_COMMAND			= 6;	/* Command */
    public final static int DX_PACKET_TYPE_FILE				= 7;	/* File Related */
    public final static int DX_PACKET_TYPE_STREAM			= 8;	/* Streaming Data */

}
