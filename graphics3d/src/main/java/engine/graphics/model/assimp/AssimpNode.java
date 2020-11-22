package engine.graphics.model.assimp;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.assimp.*;

import java.util.ArrayList;
import java.util.List;

public class AssimpNode {

    private final List<AssimpNode> children;

    private final List<Matrix4f> transformations;

    private final Matrix4f transformation;

    private final String name;

    private final AssimpNode parent;

    public AssimpNode(String name, AssimpNode parent, Matrix4f transformation) {
        children = new ArrayList<>();
        transformations = new ArrayList<>();
        this.name = name;
        this.parent = parent;
        this.transformation = transformation;
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

    public static AssimpNode processNodesHierarchy(AINode aiNode, AssimpNode parent) {
        var name = aiNode.mName().dataString();
        AssimpNode node = new AssimpNode(name, parent, AssimpHelper.generalizeNativeMatrix(aiNode.mTransformation()));

        int childCount = aiNode.mNumChildren();
        var childPointer = aiNode.mChildren();
        for (int i = 0; i < childCount; i++) {
            var childNode = AINode.create(childPointer.get(i));
            var node1 = processNodesHierarchy(childNode, node);
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

    public static Matrix4f buildTransformationMatrixByFrame(AINodeAnim aiNodeAnim, int frame) {
        AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();

        Matrix4f mat = new Matrix4f();
        int nPos = aiNodeAnim.mNumPositionKeys();
        if (nPos > 0) {
            AIVectorKey aiVecKey = positionKeys.get(Math.min(nPos - 1, frame));
            AIVector3D vec = aiVecKey.mValue();
            mat.translate(vec.x(), vec.y(), vec.z());
        }
        int nRot = aiNodeAnim.mNumRotationKeys();
        if (nRot > 0) {
            AIQuatKey quatKey = rotationKeys.get(Math.min(nRot - 1, frame));
            AIQuaternion aiQuat = quatKey.mValue();
            Quaternionf quat = new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
            mat.rotate(quat);
        }

        int nScale = aiNodeAnim.mNumScalingKeys();
        if (nScale > 0) {
            AIVectorKey aiVecKey = scalingKeys.get(Math.min(nScale - 1, frame));
            AIVector3D vec = aiVecKey.mValue();
            mat.scale(vec.x(), vec.y(), vec.z());
        }

        return mat;
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

    public Matrix4f getTransformation() {
        return transformation;
    }

    public String getName() {
        return name;
    }

    public AssimpNode getParent() {
        return parent;
    }
}
