package com.hatiolab.dx.net;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.WeakHashMap;

public class SessionManager {
	protected static WeakHashMap<SocketChannel, HashMap<String, Object>> sessions = new WeakHashMap<SocketChannel, HashMap<String, Object>>();
	
	public static HashMap<String, Object> getSession(SocketChannel channel) {
		synchronized(sessions) {
			return sessions.get(channel);
		}
	}
	
	public static HashMap<String, Object> register(SocketChannel channel) {
		synchronized(sessions) {			
			HashMap<String, Object> session = getSession(channel);
			if(session != null)
				return session;
			
			session = new HashMap<String, Object>();
			sessions.put(channel, session);
			
			return session;
		}
	}
}
