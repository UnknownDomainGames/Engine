package unknowndomain.engine.api.permission;

import java.util.HashMap;
import java.util.Map;

public class PermissionProvider implements PermissionAPI {
	
	public static final String WILDCARD = "*";
	public static final char SPLITTER = '.';
	
	private final Map<Permissable, Map<String, Boolean>> permissableToPermissions;

	public PermissionProvider() {
		permissableToPermissions = new HashMap<>();
	}

	@Override
	public boolean hasPermission(Permissable permissable, String permission) {
		Map<String, Boolean> permissions = permissableToPermissions.get(permissable);
		if (permissions == null) {
			permissableToPermissions.put(permissable, new HashMap<>());
			return false;
		}

		while (true) {
			Boolean value = permissions.get(permission);
			if (value != null)
				return value;

			if (permission.isEmpty())
				return false;

			int lastSplitIndex = permission.lastIndexOf(SPLITTER);
			permission = lastSplitIndex == -1 ? WILDCARD : permission.substring(0, lastSplitIndex);
		}
	}

	@Override
	public void setPermission(Permissable permissable, String permission, boolean value) {
		Map<String, Boolean> permissions = permissableToPermissions.get(permissable);
		if (permissions == null) {
			permissions = new HashMap<>();
			permissableToPermissions.put(permissable, permissions);
		}
		
		permissions.put(permission.endsWith(SPLITTER + WILDCARD)
				? permission.substring(0, permission.length() - 2) // Handle wildcard. "permission.*" -> "permission".
				: permission, value);
	}
}
