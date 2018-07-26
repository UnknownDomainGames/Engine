package unknowndomain.engine.api.permission;

public interface PermissionAPI {
	public boolean hasPermission(Permissable persimissable,String persimission);
	public void setPermission(Permissable persimissable,String persimission,boolean value);
}