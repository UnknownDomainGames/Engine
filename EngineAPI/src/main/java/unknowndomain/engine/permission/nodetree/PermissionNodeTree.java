package unknowndomain.engine.permission.nodetree;

public class PermissionNodeTree {
    public static final String WILDCARD = "*";
    public static final String SPLITTER = "\\.";
    private Node rootNode;

    public PermissionNodeTree() {
        this.rootNode = new Node(" ");
    }

    public boolean hasPermission(String permission) {
        String[] parts = permission.toLowerCase().split(SPLITTER);
        if (parts.length == 0) return false;
        Node tempNode = rootNode;
        for (int i = 0; i < parts.length - 1; i++) {
            Node permissionNode = tempNode.children.get(parts[i]);
            if (permissionNode != null) {
                if (permissionNode.content.equals(WILDCARD)) return true;
                if (permissionNode.content.equals(parts[i + 1]))
                    tempNode = permissionNode;
            } else {
                return false;
            }
        }
        if (tempNode.isEnd && tempNode.hasPermission)
            return true;
        return false;
    }

    public void setPermission(String permission, boolean setValue) {
        String[] parts = permission.toLowerCase().split(SPLITTER);
        Node tempNode = rootNode;
        for (int i = 0; i < parts.length - 1; i++) {
            Node value = tempNode.children.get(parts[i]);
            if (value != null && value.content.equals(parts[i])) {
                tempNode = value;
            } else {
                Node node = new Node(parts[i + 1]);
                tempNode.children.put(parts[i], node);
                tempNode = node;
            }
        }
        tempNode.hasPermission = setValue;
        tempNode.isEnd = true;
    }
}
