package com.hatiolab.dx.mplexer;

import java.nio.channels.SelectionKey;

public interface SelectableHandler {
	public void onSelected(SelectionKey key);
}
