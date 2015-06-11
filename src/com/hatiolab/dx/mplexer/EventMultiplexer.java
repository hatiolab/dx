package com.hatiolab.dx.mplexer;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

import com.hatiolab.dx.exception.PreemptiveFunctionCallError;

/**
 *
 */
public class EventMultiplexer {

	static protected EventMultiplexer uniqueMultiplexer = null;
	static protected Thread preemptiveThread;
	
	protected Selector selector = null;

	public static EventMultiplexer getInstance() throws IOException {
		if(uniqueMultiplexer == null)
			uniqueMultiplexer = new EventMultiplexer();
		return uniqueMultiplexer;
	}
	
	public static void assertPreemption() {
		if(preemptiveThread != Thread.currentThread())
			throw new PreemptiveFunctionCallError();
	}

	protected EventMultiplexer() throws IOException {
		selector = Selector.open();
	}
	
	public void poll(long timeout) throws IOException {
		int nc = selector.select(timeout);
		
		if(nc <= 0)
			return;

		preemptiveThread = Thread.currentThread();
		
		Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
		
		while (iter.hasNext()) {

			SelectionKey key = iter.next();
			Object attachment = key.attachment();
			if(attachment != null) {
				if(attachment instanceof SelectableHandler) {
					((SelectableHandler)attachment).onSelected(key);
				}
			}
		}
		selector.selectedKeys().clear();
	}
	
	public void close() throws IOException {
		this.selector.close();
		this.selector = null;
	}
	
	public Selector getSelector() {
		return selector;
	}

}
