package com.hatiolab.dx.api;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hatiolab.dx.mplexer.EventMultiplexer;
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
		
		try {
			EventMultiplexer mplexer = new EventMultiplexer();

			DxServer server = new DxServer(mplexer, 0, 3456, new PacketServerListener());

			while(true) {
				mplexer.poll(1000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
