package com.hatiolab.dx.mplexer;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 *
 */
public class EventMultiplexer {

	protected Selector selector = null;
	
	public EventMultiplexer() throws IOException {
		selector = Selector.open();
	}

	public void poll(long timeout) throws IOException {
		selector.select(timeout);

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
