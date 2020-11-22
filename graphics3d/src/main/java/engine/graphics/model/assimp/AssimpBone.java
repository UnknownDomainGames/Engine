package engine.graphics.model.assimp;

import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVertexWeight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssimpBone {

    private final int boneId;

    private final String name;

    private final Matrix4f transform;

    public AssimpBone(int id, String name, Matrix4f matrix4f) {
        boneId = id;
        this.name = name;
        this.transform = matrix4f;
    }

    public static void processBones(AssimpModel assimpModel, AIMesh aiMesh, List<Integer> boneIds,
                                    List<Float> weights) {
        Map<Integer, List<VertexWeight>> weightSet = new HashMap<>();
        int numBones = aiMesh.mNumBones();
        PointerBuffer aiBones = aiMesh.mBones();
        for (int i = 0; i < numBones; i++) {
            AIBone aiBone = AIBone.create(aiBones.get(i));
            int id = assimpModel.getBones().size();
            var bone = assimpModel.getBones().computeIfAbsent(aiBone.mName().dataString(), key -> new AssimpBone(id, aiBone.mName().dataString(), AssimpHelper.generalizeNativeMatrix(aiBone.mOffsetMatrix())));
            int numWeights = aiBone.mNumWeights();
            AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
            for (int j = 0; j < numWeights; j++) {
                AIVertexWeight aiWeight = aiWeights.get(j);
                VertexWeight vw = new VertexWeight(bone.getBoneId(), aiWeight.mVertexId(),
                        aiWeight.mWeight());
                List<VertexWeight> vertexWeightList = weightSet.computeIfAbsent(vw.getVertexId(), k -> new ArrayList<>());
                vertexWeightList.add(vw);
            }
        }

        int numVertices = aiMesh.mNumVertices();
        for (int i = 0; i < numVertices; i++) {
            List<VertexWeight> vertexWeightList = weightSet.get(i);
            int size = vertexWeightList != null ? vertexWeightList.size() : 0;
            for (int j = 0; j < AssimpMesh.MAX_WEIGHTS; j++) {
                if (j < size) {
                    VertexWeight vw = vertexWeightList.get(j);
                    weights.add(vw.getWeight());
                    boneIds.add(vw.getBoneId());
                } else {
                    weights.add(0.0f);
                    boneIds.add(0);
                }
            }
        }
    }

    public int getBoneId() {
        return boneId;
    }

    public String getName() {
        return name;
    }

    public Matrix4f getTransform() {
        return transform;
    }

    public static class VertexWeight {

        private int boneId;

        private int vertexId;

        private float weight;

        public VertexWeight(int boneId, int vertexId, float weight) {
            this.boneId = boneId;
            this.vertexId = vertexId;
            this.weight = weight;
        }

        public int getBoneId() {
            return boneId;
        }

        public int getVertexId() {
            return vertexId;
        }

        public float getWeight() {
            return weight;
        }

        public void setVertexId(int vertexId) {
            this.vertexId = vertexId;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }
    }
}
