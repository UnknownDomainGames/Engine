package engine.graphics.model.assimp;

import engine.graphics.Node3D;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.lwjgl.assimp.Assimp.aiReleaseImport;

public class AssimpModel extends Node3D {
    private final Map<String, AssimpAnimation> animations;
    private AssimpAnimation currentAnimation;
    private AIScene assimpScene;
    private Map<String, AssimpMesh> meshes;
    private Map<String, AssimpMaterial> materials;
    private final AssimpNode rootNode;

    private final Map<String, AssimpBone> bones;

    public AssimpModel(AIScene assimpScene, String filename) {
        this.assimpScene = assimpScene;
        this.bones = new HashMap<>();
        var root = assimpScene.mRootNode();
        rootNode = AssimpNode.processNodesHierarchy(root, null);
        var materials = new ArrayList<AssimpMaterial>();
        int materialCount = assimpScene.mNumMaterials();
        var materialBuf = assimpScene.mMaterials();
        for (int i = 0; i < materialCount; i++) {
            materials.add(new AssimpMaterial(assimpScene, AIMaterial.create(materialBuf.get(i)), filename));
        }
        try (var stack = MemoryStack.stackPush()) {
            final IntBuffer c = stack.mallocInt(1);
            c.put(0);
            this.materials = materials.stream().collect(Collectors.toMap(mat -> {
                if (mat.getName().isBlank()) {
                    String s = String.format("Material_#%d", c.get(0));
                    mat.assignName(s);
                    c.put(0, c.get(0) + 1);
                    return s;
                }
                return mat.getName();
            }, mat -> mat));
            int meshCount = assimpScene.mNumMeshes();
            PointerBuffer meshesBuffer = assimpScene.mMeshes();
            var meshes = new ArrayList<AssimpMesh>();
            for (int i = 0; i < meshCount; ++i) {
                var mesh = new AssimpMesh(this, AIMesh.create(meshesBuffer.get(i)));
                var assimpMaterial = materials.get(mesh.getRawMesh().mMaterialIndex());
                mesh.assignMaterialName(assimpMaterial.getName());
                mesh.setMaterial(assimpMaterial.getEngineMaterial());
                addChild(mesh);
                meshes.add(mesh);
            }
            c.put(0, 0);
            this.meshes = new HashMap<>();
            for (List<AssimpMesh> value : meshes.stream().collect(Collectors.groupingBy(AssimpMesh::getName)).values()) {
                if (value.size() > 1) {
                    var atomInt = new AtomicInteger(1);
                    for (AssimpMesh mesh : value) {
                        var s = (mesh.getName().isBlank() ? "Mesh_" : mesh.getName()) + "#" + atomInt.getAndIncrement();
                        this.meshes.put(s, mesh);
                    }
                } else {
                    this.meshes.put(value.get(0).getName(), value.get(0));
                }
            }
            var transformMatrix = AssimpHelper.generalizeNativeMatrix(root.mTransformation()).invert();
            var boneList = new ArrayList<>(bones.values());
            animations = AssimpAnimation.processAnimations(assimpScene, boneList, rootNode, transformMatrix);
            animations.entrySet().stream().findFirst().ifPresentOrElse(entry -> currentAnimation = entry.getValue(), () -> currentAnimation = AssimpAnimation.buildDefaultAnimation(rootNode, boneList, transformMatrix));
        }
    }

    public void free() {
        aiReleaseImport(assimpScene);
        assimpScene = null;
        meshes = null;
        materials = null;
    }

    public AssimpNode getRootNode() {
        return rootNode;
    }

    public Map<String, AssimpBone> getBones() {
        return bones;
    }

    public Map<String, AssimpMaterial> getMaterials() {
        return materials;
    }

    public void setCurrentAnimation(AssimpAnimation currentAnimation) {
        this.currentAnimation = currentAnimation;
    }

    public void setCurrentAnimation(String animationName) {
        animations.entrySet().stream().filter(entry -> entry.getKey().equals(animationName)).findFirst().ifPresent(entry -> setCurrentAnimation(entry.getValue()));
    }

    public AssimpAnimation getCurrentAnimation() {
        return currentAnimation;
    }

}
