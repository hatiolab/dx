package com.hatiolab.dx.net;

import java.io.IOException;
import java.net.InetAddress;

public interface DiscoveryListener {
	public void onFoundServer(InetAddress address, int port) throws IOException;
}
