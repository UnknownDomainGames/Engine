package unknowndomain.engine.client.rendering.model.assimp;

import org.lwjgl.assimp.AINode;

public class AssimpNode {


    public AssimpNode(String name, AssimpNode parent) {

    }

    public static AssimpNode processNodesHierachy(AINode aiNode, AssimpNode parent){
        var name = aiNode.mName().dataString();
        AssimpNode node = new AssimpNode(name,parent);

        int childCount = aiNode.mNumChildren();
        var childPointer = aiNode.mChildren();
        for (int i = 0; i < childCount; i++) {
            var childNode = AINode.create(childPointer.get(i));
            var node1 = processNodesHierachy(childNode, node);
            node.addChild(node1);
        }
        return node;
    }

    private void addChild(AssimpNode node1) {
    }
}
