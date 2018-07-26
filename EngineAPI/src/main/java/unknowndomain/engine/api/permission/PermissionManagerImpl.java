package unknowndomain.engine.api.permission;

import java.util.HashMap;

public class PermissionManagerImpl implements PersimissionManager{
	public static final String WILDCARD="*";
	public static final String SPLITTER=".";
	
	private final HashMap<Permissable,HashMap<String,Boolean>> rootPermissionDataMap;
	public PermissionManagerImpl() {
		rootPermissionDataMap=new HashMap<>();
	}
	
	@Override
	public boolean hasPermission(Permissable permissable, String permission) {
		HashMap<String,Boolean> childPermissionDataMap=rootPermissionDataMap.get(permissable);
		if(childPermissionDataMap==null) {
			childPermissionDataMap=new HashMap<>();
			rootPermissionDataMap.put(permissable, childPermissionDataMap);
			return false;
		}
		Boolean permissionValue=childPermissionDataMap.get(permission);
		while(true) {
			if(permissionValue!=null&&permissionValue) {
				return true;
			}
			if(permission.isEmpty()) {
				Boolean wildCardValue=childPermissionDataMap.get(WILDCARD);
				return wildCardValue==null ? false:wildCardValue;
			}
			int lastSplitIndex=permission.lastIndexOf(SPLITTER);
			permission=lastSplitIndex==-1 ? "" :permission.substring(0, lastSplitIndex);
			Boolean valueWithWildCard=childPermissionDataMap.get(permission+SPLITTER+WILDCARD);
			if(valueWithWildCard!=null&&valueWithWildCard) {
				return true;
			}
		}
	}

	@Override
	public void setPermission(Permissable persimissable, String persimission, boolean value) {
		HashMap<String,Boolean> childPermissionDataMap=rootPermissionDataMap.get(persimissable);
		if(childPermissionDataMap==null) {
			childPermissionDataMap=new HashMap<>();
			rootPermissionDataMap.put(persimissable, childPermissionDataMap);
		}
		childPermissionDataMap.put(persimission, value);
	}
}