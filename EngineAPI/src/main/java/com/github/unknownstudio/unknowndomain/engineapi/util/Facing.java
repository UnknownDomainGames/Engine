package com.github.unknownstudio.unknowndomain.engineapi.util;

public enum Facing {

	NORTH(1),
	SOUTH(0),
	EAST(3),
	WEST(2),
	TOP(5),
	BOTTOM(4);
	
	private final int opposite;
	
	Facing(int opposite){
		this.opposite = opposite;
	}
	
	public Facing opposite() {
		return values()[opposite];
	}
}
