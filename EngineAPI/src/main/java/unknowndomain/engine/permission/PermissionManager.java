package unknowndomain.engine.permission;

public interface PermissionManager {
	public boolean hasPermission(Permissable permissable,String permission);
	public void setPermission(Permissable permissable,String permission,boolean value);
}