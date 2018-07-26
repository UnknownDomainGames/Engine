package unknowndomain.engine.api.permission;

import java.util.HashMap;

import unknowndomain.engine.api.permission.nodetree.PermissionNodeTree;

public class PermissionManagerTreeImpl implements PermissionManager{
	private final HashMap<Permissable,PermissionNodeTree> permissionMap;
	
	public PermissionManagerTreeImpl() {
		permissionMap=new HashMap<>();
	}
	
	@Override
	public boolean hasPermission(Permissable persimissable, String persimission) {
		PermissionNodeTree permissionNodeTree=permissionMap.get(persimissable);
		if(permissionNodeTree==null) {
			permissionMap.put(persimissable, new PermissionNodeTree());
			return false;
		}
		return permissionNodeTree.hasPermission(persimission);
	}

	@Override
	public void setPermission(Permissable permissable, String permission, boolean value) {
		PermissionNodeTree permissionNodeTree=permissionMap.get(permissable);
		if(permissionNodeTree==null) {
			permissionNodeTree=new PermissionNodeTree();
			permissionMap.put(permissable, permissionNodeTree);
		}
		permissionNodeTree.setPermission(permission, value);
	}
}
