package com.hatiolab.dx.api;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hatiolab.dx.api.DxServer;
import com.hatiolab.dx.sample.server.PacketServerListener;
import com.hatiolab.dx.sample.server.Server;

public class DxServerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		DxServer server = new DxServer();
		
		try {
			server.start(Server.PACKET_SERVICE_PORT, Server.DISCOVERY_SERVICE_PORT, new PacketServerListener());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
