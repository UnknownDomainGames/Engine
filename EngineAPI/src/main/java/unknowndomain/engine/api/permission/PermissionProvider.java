package unknowndomain.engine.api.permission;

import java.util.HashMap;

public class PermissionProvider implements PermissionAPI{
	protected HashMap<Permissable,HashMap<String,Boolean>> persimissionMap;
	private String wildCard="*";//Í¨Åä·û
	private String splitChar=".";//·Ö¸ô·û
	
	public PermissionProvider() {
		persimissionMap=new HashMap<>();
	}
	
	@Override
	public boolean hasPermission(Permissable persimissable, String persimission) {
		HashMap<String,Boolean> map=persimissionMap.get(persimissable);
		if(map==null) {
			persimissionMap.put(persimissable, new HashMap<>());
			return false;
		}
		Boolean value=map.get(persimission);
		if(value!=null&&value)return true;
		else {
			String[] strs=persimission.split("\\"+splitChar);
			persimission=strs[0];
			int temp=2;
			while(temp<=strs.length) {
				for(int i=1;i<=strs.length-temp;i++)persimission=persimission+splitChar+strs[i];
				String tempStr=persimission+splitChar+wildCard;
				if(map.containsKey(tempStr)) {
					if(map.get(tempStr))
					return true;
				}
				temp++;
			}
			return false;
		}
	}

	@Override
	public void setPermission(Permissable persimissable, String persimission, boolean value) {
		HashMap<String,Boolean> map=persimissionMap.get(persimissable);
		if(map==null) {
			map=new HashMap<>();
			persimissionMap.put(persimissable, map);
		}
		map.put(persimission, value);
	}
}
