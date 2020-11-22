package engine.graphics.model.assimp;

import engine.graphics.util.Struct;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AINodeAnim;
import org.lwjgl.assimp.AIScene;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AssimpAnimation {
    private int currentFrame;

    private List<AnimatedFrame> frames;

    private String name;

    private double duration;

    public AssimpAnimation(String name, List<AnimatedFrame> frames, double duration) {
        this.name = name;
        this.frames = frames;
        currentFrame = 0;
        this.duration = duration;
    }

    public AnimatedFrame getCurrentFrame() {
        return this.frames.get(currentFrame);
    }

    public double getDuration() {
        return this.duration;
    }

    public List<AnimatedFrame> getFrames() {
        return frames;
    }

    public String getName() {
        return name;
    }

    public AnimatedFrame getNextFrame() {
        nextFrame();
        return this.frames.get(currentFrame);
    }

    public void nextFrame() {
        int nextFrame = currentFrame + 1;
        if (nextFrame > frames.size() - 1) {
            currentFrame = 0;
        } else {
            currentFrame = nextFrame;
        }
    }

    public static Map<String, AssimpAnimation> processAnimations(AIScene aiScene, List<AssimpBone> boneList,
                                                                 AssimpNode rootNode, Matrix4f rootTransformationInverted) {
        Map<String, AssimpAnimation> animations = new HashMap<>();

        // Process all animations
        int numAnimations = aiScene.mNumAnimations();
        PointerBuffer aiAnimations = aiScene.mAnimations();
        for (int i = 0; i < numAnimations; i++) {
            AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));
            int maxFrames = 0;

            // Calculate transformation matrices for each node
            int numChannels = aiAnimation.mNumChannels();
            PointerBuffer aiChannels = aiAnimation.mChannels();
            for (int j = 0; j < numChannels; j++) {
                AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(j));
                int numFrames = Math.max(Math.max(aiNodeAnim.mNumPositionKeys(), aiNodeAnim.mNumScalingKeys()),
                        aiNodeAnim.mNumRotationKeys());
                maxFrames = Math.max(maxFrames, numFrames);
            }

            List<AnimatedFrame> frames = buildAnimationFrames(aiAnimation, maxFrames, boneList, rootNode, rootTransformationInverted);
            var animation = new AssimpAnimation(aiAnimation.mName().dataString(), frames, aiAnimation.mDuration());
            animations.put(animation.getName(), animation);
        }
        return animations;
    }

    public static List<AnimatedFrame> buildAnimationFrames(AIAnimation aiAnimation, int maxFrames, List<AssimpBone> boneList, AssimpNode rootNode,
                                                           Matrix4f globalTransform) {

        List<AnimatedFrame> frameList = new ArrayList<>();
        for (int i = 0; i < maxFrames; i++) {
            AnimatedFrame frame = new AnimatedFrame();
            buildAnimationFrame(aiAnimation, frame, boneList, i, rootNode, rootNode.getTransformation(), globalTransform);
            frameList.add(frame);
        }

        return frameList;
    }

    private static AINodeAnim findAnimateNode(AIAnimation aiAnimation, String name) {
        if (aiAnimation == null) return null;
        int numChannels = aiAnimation.mNumChannels();
        PointerBuffer aiChannels = aiAnimation.mChannels();
        for (int j = 0; j < numChannels; j++) {
            AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(j));
            if (name.equals(aiNodeAnim.mNodeName().dataString())) {
                return aiNodeAnim;
            }
        }
        return null;
    }

    private static void buildAnimationFrame(AIAnimation aiAnimation, AnimatedFrame animatedFrame, List<AssimpBone> bones, int frame, AssimpNode node, Matrix4f parentMat, Matrix4f globalMat) {
        String nodeName = node.getName();
        AINodeAnim nodeAnim = findAnimateNode(aiAnimation, nodeName);
        Matrix4f nodeMat = node.getTransformation();
        if (nodeAnim != null) {
            nodeMat = AssimpNode.buildTransformationMatrixByFrame(nodeAnim, frame);
        }
        Matrix4f nodeGT = new Matrix4f(parentMat).mul(nodeMat);
        var boneList = bones.stream().filter(bone -> bone.getName().equals(nodeName)).collect(Collectors.toList());
        for (AssimpBone bone : boneList) {
            animatedFrame.setMatrix(bone.getBoneId(), new Matrix4f(globalMat).mul(nodeGT).mul(bone.getTransform()));
        }
        for (AssimpNode child : node.getChildren()) {
            buildAnimationFrame(aiAnimation, animatedFrame, bones, frame, child, nodeGT, globalMat);
        }
    }

    public static AssimpAnimation buildDefaultAnimation(AssimpNode rootNode, List<AssimpBone> bones, Matrix4f globalInverseTransform) {
        return new AssimpAnimation("identity", buildAnimationFrames(null, 1, bones, rootNode, globalInverseTransform), 1);
    }

    public static AnimatedFrame buildIdentityFrame(int boneCount) {
        AnimatedFrame frame = new AnimatedFrame();
        for (int i = 0; i < boneCount; i++) {
            frame.setMatrix(i, new Matrix4f());
        }
        return frame;
    }

    public static class AnimatedFrame {

        private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();

        public static final int MAX_JOINTS = 300;

        private final Matrix4f[] jointMatrices;

        public AnimatedFrame() {
            jointMatrices = new Matrix4f[MAX_JOINTS];
            Arrays.fill(jointMatrices, IDENTITY_MATRIX);
        }

        public Matrix4f[] getJointMatrices() {
            return jointMatrices;
        }

        public void setMatrix(int pos, Matrix4f jointMatrix) {
            if (pos >= jointMatrices.length) {
                Logger.getGlobal().warning(pos + ": too large as joint!");
                return;
            }
            jointMatrices[pos] = jointMatrix;
        }
    }

    public static class StructBones implements Struct {

        private Matrix4f[] bones;

        public StructBones(Matrix4f[] bones) {
            this.bones = bones;
        }

        @Override
        public int sizeof() {
            return getClampedBoneSize() * 64;
        }

        @Override
        public ByteBuffer get(int index, ByteBuffer buffer) {
            for (int i = 0; i < getClampedBoneSize(); i++) {
                bones[i].get(index + i * 64, buffer);
            }
            return buffer;
        }

        public int getClampedBoneSize() {
            return Math.min(bones.length, 300);
        }
    }
}
