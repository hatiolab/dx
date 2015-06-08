package com.hatiolab.dx.mplexer;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hatiolab.dx.mplexer.EventMultiplexer;

public class EventMultiplexerTest {

	EventMultiplexer mplexer;
	
	@Before
	public void setUp() throws Exception {
		mplexer = new EventMultiplexer();
	}

	@After
	public void tearDown() throws Exception {
		mplexer = null;
	}

	@Test
	public void test() {
		try {
			for(int i = 0;i < 4;i++) {
				mplexer.poll(1000);
			}

			mplexer.close();
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		}

		assertTrue(true);
	}

}
