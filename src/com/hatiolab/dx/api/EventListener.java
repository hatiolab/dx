package com.hatiolab.dx.api;

import com.hatiolab.dx.packet.Data;
import com.hatiolab.dx.packet.Header;

public interface EventListener {
	public void	onDxEvent(Header header, Data data); 
}
