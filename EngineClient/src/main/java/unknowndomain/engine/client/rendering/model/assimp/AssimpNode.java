package unknowndomain.engine.client.rendering.model.assimp;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.assimp.*;

import java.util.ArrayList;
import java.util.List;

public class AssimpNode {

    private final List<AssimpNode> children;

    private final List<Matrix4f> transformations;

    private final String name;

    private final AssimpNode parent;

    public AssimpNode(String name, AssimpNode parent) {
        children = new ArrayList<>();
        transformations = new ArrayList<>();
        this.name = name;
        this.parent = parent;
    }

    public static Matrix4f getParentTransforms(AssimpNode node, int framePos) {
        if (node == null) {
            return new Matrix4f();
        } else {
            Matrix4f parentTransform = new Matrix4f(getParentTransforms(node.getParent(), framePos));
            List<Matrix4f> transformations = node.getTransformations();
            Matrix4f nodeTransform;
            int transfSize = transformations.size();
            if (framePos < transfSize) {
                nodeTransform = transformations.get(framePos);
            } else if (transfSize > 0) {
                nodeTransform = transformations.get(transfSize - 1);
            } else {
                nodeTransform = new Matrix4f();
            }
            return parentTransform.mul(nodeTransform);
        }
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

    public static void buildTransFormationMatrices(AINodeAnim aiNodeAnim, AssimpNode node) {
        int numFrames = aiNodeAnim.mNumPositionKeys();
        AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();

        for (int i = 0; i < numFrames; i++) {
            AIVectorKey aiVecKey = positionKeys.get(i);
            AIVector3D vec = aiVecKey.mValue();

            Matrix4f transfMat = new Matrix4f().translate(vec.x(), vec.y(), vec.z());

            AIQuatKey quatKey = rotationKeys.get(i);
            AIQuaternion aiQuat = quatKey.mValue();
            Quaternionf quat = new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
            transfMat.rotate(quat);

            if (i < aiNodeAnim.mNumScalingKeys()) {
                aiVecKey = scalingKeys.get(i);
                vec = aiVecKey.mValue();
                transfMat.scale(vec.x(), vec.y(), vec.z());
            }

            node.addTransformation(transfMat);
        }
    }

    public void addChild(AssimpNode node1) {
        children.add(node1);
    }

    public AssimpNode findChildByName(String name) {
        AssimpNode result = null;
        if (this.name.equals(name)) {
            return this;
        } else {
            for (AssimpNode child : children) {
                result = child.findChildByName(name);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    public int getAnimationFrames() {
        int numFrames = this.transformations.size();
        for (AssimpNode child : children) {
            int childFrame = child.getAnimationFrames();
            numFrames = Math.max(numFrames, childFrame);
        }
        return numFrames;
    }

    public void addTransformation(Matrix4f mat) {
        transformations.add(mat);
    }

    public List<AssimpNode> getChildren() {
        return children;
    }

    public List<Matrix4f> getTransformations() {
        return transformations;
    }

    public String getName() {
        return name;
    }

    public AssimpNode getParent() {
        return parent;
    }
}
