package com.hatiolab.dx.sample.server;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Server server = new Server();
		
		try {
			server.start(Server.PACKET_SERVICE_PORT, Server.DISCOVERY_SERVICE_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
