package unknowndomain.engine.api.permission.nodetree;

public class PermissionNodeTree {
	private Node rootNode;
	public static final String WILDCARD="*";
	public static final String SPLITTER="\\.";
	public PermissionNodeTree() {
		this.rootNode=new Node(" ");
	}
	public boolean hasPermission(String permission) {
		String[] parts=permission.toLowerCase().split(SPLITTER);
		if(parts.length==0)return false;
		Node tempRootNode=rootNode;
		for(int i=0;i<parts.length-1;i++) {
			Node value=tempRootNode.children.get(parts[i]);
			if(value!=null) {
				if(value.content.equals(WILDCARD))return true;//通配�?
				if(value.content.equals(parts[i+1]))
					tempRootNode=value;
			}else {
				return false;
			}
		}
		if(tempRootNode.isEnd&&tempRootNode.hasPermission)
			return true;
		return false;
	}
	public void setPermission(String permission,boolean setValue) {
		String[] parts=permission.toLowerCase().split(SPLITTER);
		Node tempRootNode=rootNode;
		for(int i=0;i<parts.length-1;i++) {
			Node value=tempRootNode.children.get(parts[i]);
			if(value!=null&&value.content.equals(parts[i])) {
				tempRootNode=value;
			}else {
				Node node =new Node(parts[i+1]);
				tempRootNode.children.put(parts[i], node);
				tempRootNode=node;
			}
		}
		tempRootNode.hasPermission=setValue;
		tempRootNode.isEnd=true;
	}
}
