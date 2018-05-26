package com.github.unknownstudio.unknowndomain.engineapi.event;

public interface Cancellable {
	
	boolean isCancelled();
	
	void setCancelled(boolean cancelled);
}
