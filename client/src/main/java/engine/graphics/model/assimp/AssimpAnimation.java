package engine.graphics.model.assimp;

import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AINodeAnim;
import org.lwjgl.assimp.AIScene;

import java.util.*;

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
                                                                 AssimpNode rootNode, Matrix4f rootTransformation) {
        Map<String, AssimpAnimation> animations = new HashMap<>();

        // Process all animations
        int numAnimations = aiScene.mNumAnimations();
        PointerBuffer aiAnimations = aiScene.mAnimations();
        for (int i = 0; i < numAnimations; i++) {
            AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));

            // Calculate transformation matrices for each node
            int numChanels = aiAnimation.mNumChannels();
            PointerBuffer aiChannels = aiAnimation.mChannels();
            for (int j = 0; j < numChanels; j++) {
                AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(j));
                String nodeName = aiNodeAnim.mNodeName().dataString();
                AssimpNode node = rootNode.findChildByName(nodeName);
                AssimpNode.buildTransFormationMatrices(aiNodeAnim, node);
            }

            List<AnimatedFrame> frames = buildAnimationFrames(boneList, rootNode, rootTransformation);
            var animation = new AssimpAnimation(aiAnimation.mName().dataString(), frames, aiAnimation.mDuration());
            animations.put(animation.getName(), animation);
        }
        return animations;
    }

    public static List<AnimatedFrame> buildAnimationFrames(List<AssimpBone> boneList, AssimpNode rootNode,
                                                           Matrix4f rootTransformation) {

        int numFrames = rootNode.getAnimationFrames();
        List<AnimatedFrame> frameList = new ArrayList<>();
        for (int i = 0; i < numFrames; i++) {
            AnimatedFrame frame = new AnimatedFrame();
            frameList.add(frame);

            int numBones = boneList.size();
            for (int j = 0; j < numBones; j++) {
                var bone = boneList.get(j);
                var node = rootNode.findChildByName(bone.getName());
                Matrix4f boneMatrix = AssimpNode.getParentTransforms(node, i);
                boneMatrix.mul(bone.getTransform());
                boneMatrix = new Matrix4f(rootTransformation).mul(boneMatrix);
                frame.setMatrix(j, boneMatrix);
            }
        }

        return frameList;
    }

    public static AssimpAnimation buildDefaultAnimation(int boneCount) {
        return new AssimpAnimation("identity", List.of(buildIdentityFrame(boneCount)), 1);
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
            jointMatrices[pos] = jointMatrix;
        }
    }

}
