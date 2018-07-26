package unknowndomain.engine.api.permission;

public interface PersimissionManager {
	public boolean hasPersimission(Permissable permissable,String permission);
	public void setPersimission(Permissable permissable,String permission,boolean value);
}