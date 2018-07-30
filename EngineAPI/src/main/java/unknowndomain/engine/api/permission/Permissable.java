package unknowndomain.engine.api.permission;

public interface Permissable {
	boolean hasPermission(String permission);
	
	void setPermission(String permission, boolean value);
	
	boolean removePermission(String permission);
}
