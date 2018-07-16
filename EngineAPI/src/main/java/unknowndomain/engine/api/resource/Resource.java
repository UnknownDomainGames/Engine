package unknowndomain.engine.api.resource;

public interface Resource {
	/**
	 * 
	 * @return content whitch you want to write
	 */
	public byte[] getContent();
	/**
	 * 
	 * @return resource path
	 */
	public String getPath();
}
