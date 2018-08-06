package unknowndomain.engine.permission;

import java.util.HashMap;

public class PermissionManagerMapImpl implements PermissionManager {
	public static final String WILDCARD="*";
	public static final String SPLITTER=".";
	private final HashMap<Permissable,HashMap<String,Boolean>> rootPermissionDataMap;
	
	public PermissionManagerMapImpl() {
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
			Boolean valueWithWildCard=childPermissionDataMap.get(permission);
			if(valueWithWildCard!=null&&valueWithWildCard) {
				return true;
			}
		}
	}

	@Override
	public void setPermission(Permissable permissable, String permission, boolean value) {
		HashMap<String,Boolean> childPermissionDataMap=rootPermissionDataMap.get(permissable);
		if(childPermissionDataMap==null) {
			childPermissionDataMap=new HashMap<>();
			rootPermissionDataMap.put(permissable, childPermissionDataMap);
		}
		childPermissionDataMap.put(permission.endsWith(SPLITTER+WILDCARD)
			?permission.substring(0,permission.length()-2)//Handle wildcard. "permission.*" -> "permission".
			:permission, value);
	}
}