package engine.graphics.model.assimp;

import engine.graphics.Node3D;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.lwjgl.assimp.Assimp.aiReleaseImport;

public class AssimpModel extends Node3D {
    private final Map<String, AssimpAnimation> animations;
    private AssimpAnimation currentAnimation;
    private AIScene assimpScene;
    private Map<String, AssimpMesh> meshes;
    private Map<String, AssimpMaterial> materials;

    public AssimpModel(AIScene assimpScene, String filename) {
        this.assimpScene = assimpScene;
        var materials = new ArrayList<AssimpMaterial>();
        int materialCount = assimpScene.mNumMaterials();
        var materialBuf = assimpScene.mMaterials();
        for (int i = 0; i < materialCount; i++) {
            materials.add(new AssimpMaterial(assimpScene, AIMaterial.create(materialBuf.get(i)), filename));
        }
        try(var stack = MemoryStack.stackPush()) {
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
                var mesh = new AssimpMesh(AIMesh.create(meshesBuffer.get(i)));
                mesh.assignMaterialName(materials.get(mesh.getRawMesh().mMaterialIndex()).getName());
                addChild(mesh);
                meshes.add(mesh);
            }
            c.put(0, 0);
            this.meshes = meshes.stream().collect(Collectors.toMap(mesh -> {
                if (mesh.getName().isBlank()) {
                    String s = String.format("Mesh_#%d", c.get(0));
                    c.put(0, c.get(0) + 1);
                    return s;
                }
                return mesh.getName();
            }, mesh -> mesh));
            List<AssimpBone> allbones = meshes.stream().flatMap(mesh -> mesh.getBones().stream()).collect(Collectors.toList());
            var root = assimpScene.mRootNode();
            var transforMatrix = AssimpHelper.generalizeNativeMatrix(root.mTransformation());
            var rootNode = AssimpNode.processNodesHierachy(root, null);
            animations = AssimpAnimation.processAnimations(assimpScene, allbones, rootNode, transforMatrix);
            animations.entrySet().stream().findFirst().ifPresentOrElse(entry -> currentAnimation = entry.getValue(), () -> currentAnimation = AssimpAnimation.buildDefaultAnimation(allbones.size()));
        }
    }

    public void free() {
        aiReleaseImport(assimpScene);
        assimpScene = null;
        meshes = null;
        materials = null;
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
