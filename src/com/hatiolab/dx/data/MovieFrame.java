package com.hatiolab.dx.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class MovieFrame extends Data {
	String path;
	int frameno;
	int frameLength;
	int flags;
	int indexCount;
	List<MovieTrackIndex> trackIndexList;
	byte[] track;
	
	public MovieFrame() {
		// do nothing
	}
	
	public MovieFrame(String path, int frameno, int frameLength, int flags, int indexCount) {
		this.path = path;
		this.frameno = frameno;
		this.frameLength = frameLength;
		this.flags = flags;
		this.indexCount = indexCount;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getFrameno() {
		return frameno;
	}

	public void setFrameno(int frameno) {
		this.frameno = frameno;
	}

	public int getFrameLength() {
		return frameLength;
	}

	public void setFrameLength(int frameLength) {
		this.frameLength = frameLength;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public int getIndexCount() {
		return indexCount;
	}

	public void setIndexCount(int indexCount) {
		this.indexCount = indexCount;
	}
	
	public List<MovieTrackIndex> getTrackIndexList() {
		if (this.trackIndexList == null) {
			setTrackIndexList(new ArrayList<MovieTrackIndex>());
		}
		return trackIndexList;
	}

	public void setTrackIndexList(List<MovieTrackIndex> trackIndexList) {
		this.trackIndexList = trackIndexList;
	}

	public byte[] getTrack() {
		return track;
	}

	public void setTrack(byte[] track) {
		this.track = track;
	}

	@Override
	public int unmarshalling(byte[] buf, int offset) throws IOException {
		this.path = Util.readString(buf, offset, Data.PATH_MAX_SIZE);
		this.frameno = (int)Util.readU32(buf, offset + Data.PATH_MAX_SIZE);
		this.frameLength = (int)Util.readU32(buf, offset + Data.PATH_MAX_SIZE + 4);
		this.flags = (int)Util.readU8(buf, offset + Data.PATH_MAX_SIZE + 4 + 4);
		this.indexCount = (int)Util.readU8(buf, offset + Data.PATH_MAX_SIZE + 4 + 4 + 1);
		
		boolean lastFlag = false;
		int i = 0;
		int pos = offset + Data.PATH_MAX_SIZE + 4 + 4 + 1 + 1;
		while (!lastFlag) {
			MovieTrackIndex track = new MovieTrackIndex();
			byte[] trackIndex = new byte[getByteLength() - pos];
			System.arraycopy(buf, pos, trackIndex, 0, track.getByteLength());
			track.unmarshalling(trackIndex, 0);
			this.getTrackIndexList().add(track);
			
			i++;
			
			pos = pos + track.getByteLength() * i;
			if (pos == getByteLength() - 1) {
				lastFlag = true;
			}
		}
		
		this.track = new byte[getByteLength() - pos];
		System.arraycopy(buf, pos, track, 0, getByteLength() - pos);
		
		return getByteLength();
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {

		if(offset + getByteLength() > buf.length)
			throw new IOException("OutOfBound");

		Util.writeString(this.path, buf, offset, Data.PATH_MAX_SIZE);
		Util.writeU32(this.frameno, buf, offset + Data.PATH_MAX_SIZE);
		Util.writeU32(this.frameLength, buf, offset + Data.PATH_MAX_SIZE + 4);
		Util.writeU8((short)this.flags, buf, offset + Data.PATH_MAX_SIZE + 4 + 4);
		Util.writeU8((short)this.indexCount, buf, offset + Data.PATH_MAX_SIZE + 4 + 4 + 1);

		return getByteLength();
	}
	
	@Override
	public void unmarshalling(ByteBuffer buf) throws IOException {
		this.path = Util.readString(buf, PATH_MAX_SIZE);
		this.frameno = (int)Util.readU32(buf);
		this.frameLength = (int)Util.readU32(buf);
		this.flags = (int)Util.readU8(buf);
		this.indexCount = (int)Util.readU8(buf);
		
		for(int i = 0; i < indexCount; i++) {
			MovieTrackIndex track = new MovieTrackIndex();
			track.unmarshalling(buf);
			this.getTrackIndexList().add(track);
		}
		
		track = new byte[frameLength];
		buf.get(track);
	}

	@Override
	public void marshalling(ByteBuffer buf) throws IOException {

		//	TODO
	}

	@Override
	public int getByteLength() {
		return this.frameLength;
	}
}
