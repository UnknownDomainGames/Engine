package unknowndomain.engine.permission.nodetree;

import java.util.HashMap;
import java.util.Map;

public class Node {
	public Map<String,Node> children;
	public String content=" ";
	public boolean hasPermission=false;
	public boolean isEnd=false;
	public Node(String content) {
		this(new HashMap<>());
		this.content=content;
	}
	public Node(Map<String,Node> children) {
		this.children=children;
	}
}
