package unknowndomain.engine.util;

public enum Facing {

	/**
	 * Positive x-axis
	 */
	NORTH(1),
	/**
	 * Negative x-axis
	 */
	SOUTH(0),
	/**
	 * Positive z-axis
	 */
	EAST(3),
	/**
	 * Negative z-axis
	 */
	WEST(2),
	/**
	 * Positive y-axis
	 */
	TOP(5),
	/**
	 * Negative y-axis
	 */
	BOTTOM(4);

	private final int opposite;

	Facing(int opposite) {
		this.opposite = opposite;
	}

	public Facing opposite() {
		return values()[opposite];
	}
}
