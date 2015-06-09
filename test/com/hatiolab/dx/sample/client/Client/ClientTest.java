package com.hatiolab.dx.sample.client.Client;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hatiolab.dx.sample.server.Server;

public class ClientTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Client client = new Client();
		
		try {
			client.start(Server.DISCOVERY_SERVICE_PORT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
