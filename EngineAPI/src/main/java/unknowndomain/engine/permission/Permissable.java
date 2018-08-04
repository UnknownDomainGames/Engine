package unknowndomain.engine.permission;

public interface Permissable {
	boolean hasPermission(String permission);
	
	void addPermission(String permission);
	
	boolean removePermission(String permission);
}
