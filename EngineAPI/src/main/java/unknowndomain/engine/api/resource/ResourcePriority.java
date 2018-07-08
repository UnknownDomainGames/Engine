package unknowndomain.engine.api.resource;

public enum ResourcePriority {
	LOWEST(0), LOW(1), NORMAL(2), HIGH(3), HIGHEST(4);
	/**
	 * order
	 */
	private int order;
	/**
	 * 
	 * @param priority
	 */
	private ResourcePriority(int priority) {
		this.order = priority;
	}
	/**
	 * 
	 * @return priority's order
	 */
	public int order() {
		return this.order;
	}
	
}
