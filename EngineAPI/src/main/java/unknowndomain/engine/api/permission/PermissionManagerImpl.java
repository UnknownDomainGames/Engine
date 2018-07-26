package unknowndomain.engine.api.permission;

import java.util.HashMap;

public class PermissionManagerImpl implements PersimissionManager{
	protected HashMap<Permissable,HashMap<String,Boolean>> rootPermissionDataMap;
	private String wildCard="*";
	private String splitChar=".";
	
	public PermissionManagerImpl() {
		rootPermissionDataMap=new HashMap<>();
	}
	
	@Override
	public boolean hasPersimission(Permissable permissable, String permission) {
		HashMap<String,Boolean> childPermissionDataMap=rootPermissionDataMap.get(permissable);
		if(childPermissionDataMap==null) {
			rootPermissionDataMap.put(permissable, new HashMap<>());
			return false;
		}
		Boolean permissionValue=childPermissionDataMap.get(permission);
		if(permissionValue!=null&&permissionValue) {
			return true;
		}else {
			String[] permissionSplitArray=permission.split("\\.");
			String permissionWithWildChar=permissionSplitArray[0]+splitChar;//有通配符
			for(int i=1;i<permissionSplitArray.length;i++) {
				Boolean permissionWithWildCharValue=childPermissionDataMap.get(permissionWithWildChar+wildCard);//带有通配符的权限
				if(permissionWithWildCharValue!=null&&permissionWithWildCharValue) {
					return true;
				}
				permissionWithWildChar=permissionWithWildChar+permissionSplitArray[i]+splitChar;
			}
			return false;
		}
	}

	@Override
	public void setPersimission(Permissable persimissable, String persimission, boolean value) {
		HashMap<String,Boolean> childPermissionDataMap=rootPermissionDataMap.get(persimissable);
		if(childPermissionDataMap==null) {
			childPermissionDataMap=new HashMap<>();
			rootPermissionDataMap.put(persimissable, childPermissionDataMap);
		}
		childPermissionDataMap.put(persimission, value);
	}
}