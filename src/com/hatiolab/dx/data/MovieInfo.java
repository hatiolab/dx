package com.hatiolab.dx.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.hatiolab.dx.net.Util;
import com.hatiolab.dx.packet.Data;

public class MovieInfo extends Data {
	String path;
	int totalFrame;
	int totalFragment;
	int framesPerSec;
	int playTime;
	int width;
	int height;
	int trackCount;
	List<MovieTrackInfo> trackInfoList;
	
	public MovieInfo() {
		// do nothing
	}
	
	public MovieInfo(String path, int totalFrame, int totalFragment, int framesPerSec,
			int playTime, int width, int height, int trackCount, List<MovieTrackInfo> trackInfoList) {
		this.path = path;
		this.totalFrame = totalFrame;
		this.totalFragment = totalFragment;
		this.framesPerSec = framesPerSec;
		this.playTime = playTime;
		this.width = width;
		this.height = height;
		this.trackCount = trackCount;
		this.trackInfoList = trackInfoList;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getTotalFrame() {
		return totalFrame;
	}

	public void setTotalFrame(int totalFrame) {
		this.totalFrame = totalFrame;
	}

	public int getTotalFragment() {
		return totalFragment;
	}

	public void setTotalFragment(int totalFragment) {
		this.totalFragment = totalFragment;
	}

	public int getFramesPerSec() {
		return framesPerSec;
	}

	public void setFramesPerSec(int framesPerSec) {
		this.framesPerSec = framesPerSec;
	}

	public int getPlayTime() {
		return playTime;
	}

	public void setPlayTime(int playTime) {
		this.playTime = playTime;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getTrackCount() {
		return trackCount;
	}

	public void setTrackCount(int trackCount) {
		this.trackCount = trackCount;
	}
	
	public List<MovieTrackInfo> getTrackInfoList() {
		if (this.trackInfoList == null) {
			setTrackInfoList(new ArrayList<MovieTrackInfo>());
		}
		
		return trackInfoList;
	}

	public void setTrackInfoList(List<MovieTrackInfo> trackInfoList) {
		this.trackInfoList = trackInfoList;
	}

	@Override
	public int unmarshalling(byte[] buf, int offset) throws IOException {
		this.path = Util.readString(buf, offset, PATH_MAX_SIZE);
		this.totalFrame = (int)Util.readU32(buf, offset + PATH_MAX_SIZE);
		this.totalFragment = (int)Util.readU32(buf, offset + PATH_MAX_SIZE + 4);
		this.framesPerSec = (int)Util.readU16(buf, offset + PATH_MAX_SIZE + 4 + 4);
		this.playTime = (int)Util.readU16(buf, offset + PATH_MAX_SIZE + 4 + 4 + 2);
		this.width = (int)Util.readU16(buf, offset + PATH_MAX_SIZE + 4 + 4 + 2 + 2);
		this.height = (int)Util.readU16(buf, offset + PATH_MAX_SIZE + 4 + 4 + 2 + 2 + 2);
		this.trackCount = (int)Util.readU8(buf, offset + PATH_MAX_SIZE + 4 + 4 + 2 + 2 + 2 + 2);
		
		int pos = offset + PATH_MAX_SIZE + 4 + 4 + 2 + 2 + 2 + 2 + 1;
		
		boolean lastFlag = false;
		int i = 0;
		while (!lastFlag) {
			byte[] trackBuf = new byte[buf.length - pos];
			System.arraycopy(buf, pos, trackBuf, 0, trackBuf.length);
			MovieTrackInfo track = new MovieTrackInfo();
			
			track.unmarshalling(trackBuf, 0);
			this.getTrackInfoList().add(track);
			
			i++;
			
			pos = pos + 16 * i;
			if (pos == buf.length - 1) {
				lastFlag = true;
			}
		}
		
		return buf.length;
	}

	@Override
	public int marshalling(byte[] buf, int offset) throws IOException {
		// TODO

		return getByteLength();
	}
	
	@Override
	public void unmarshalling(ByteBuffer buf) throws IOException {
		this.path = Util.readString(buf, PATH_MAX_SIZE);
		this.totalFrame = (int)Util.readU32(buf);
		this.totalFragment = (int)Util.readU32(buf);
		this.framesPerSec = (int)Util.readU16(buf);
		this.playTime = (int)Util.readU16(buf);
		this.width = (int)Util.readU16(buf);
		this.height = (int)Util.readU16(buf);
		this.trackCount = (int)Util.readU8(buf);
		
		boolean lastFlag = false;
		while (!lastFlag) {
			MovieTrackInfo track = new MovieTrackInfo();
			track.unmarshalling(buf);
			
			this.getTrackInfoList().add(track);
			
			if (buf.position() == buf.limit()) {
				lastFlag = true;
			}
		}
	}

	@Override
	public void marshalling(ByteBuffer buf) throws IOException {

		//	TODO
	}

	@Override
	public int getByteLength() {
		return 0;
	}
}
