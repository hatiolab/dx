package com.hatiolab.dx.mplexer;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.hatiolab.dx.exception.PreemptiveFunctionCallError;

/**
 *
 */
public class EventMultiplexer {

	static protected EventMultiplexer uniqueMultiplexer = null;
	static protected Thread preemptiveThread;
	
	protected Selector selector;
	
	protected Queue<Runnable> runnableList;

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
		runnableList = new ConcurrentLinkedQueue<Runnable>();
		selector = Selector.open();
	}
	
	public void poll(long timeout) throws IOException {
		preemptiveThread = Thread.currentThread();
		
		if(selector.select(timeout) > 0) {
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
		while(runnableList.size() > 0) {
			Runnable runnable = runnableList.poll();
			runnable.run();
		}
	}
	
	public void close() throws IOException {
		this.selector.close();
	}
	
	public Selector getSelector() {
		return selector;
	}
	
	public void deligate(Runnable runnable) {
		runnableList.add(runnable);
		wakeup();
	}
	
	private class RegisterJob implements Runnable {
		SelectableChannel channel;
		int ops;
		SelectableHandler handler;
		
		RegisterJob(SelectableChannel channel, int ops, SelectableHandler handler) {
			this.channel = channel;
			this.ops = ops;
			this.handler = handler;
		}
		
		@Override
		public void run() {
			try {
				SelectionKey key = channel.register(selector, ops);
				key.attach(handler);
			} catch (ClosedChannelException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void register(SelectableChannel channel, int ops, SelectableHandler handler) {
		deligate(new RegisterJob(channel, ops, handler));
	}

	public void wakeup() {
		if (selector != null && Thread.currentThread() != preemptiveThread) {		
			selector.wakeup();
		}
	}
}
