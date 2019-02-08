package unknowndomain.engine.client.rendering.model.assimp;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import unknowndomain.engine.client.rendering.shader.ShaderManager;
import unknowndomain.engine.client.rendering.util.GLDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.lwjgl.assimp.Assimp.aiReleaseImport;

public class AssimpModel {
    private final Map<String, AssimpAnimation> animations;
    private AssimpAnimation currentAnimation;
    private AIScene scene;
    private List<AssimpMesh> meshes;
    private List<AssimpMaterial> materials;

    private int vaoid;

    public AssimpModel(AIScene scene, String parentDir){
        this.scene = scene;
        materials = new ArrayList<>();
        int materialCount = scene.mNumMaterials();
        var materialBuf = scene.mMaterials();
        for (int i = 0; i < materialCount; i++) {
            materials.add(new AssimpMaterial(AIMaterial.create(materialBuf.get(i)), parentDir));
        }
        int meshCount = scene.mNumMeshes();
        PointerBuffer meshesBuffer = scene.mMeshes();
        meshes = new ArrayList<>();
        for (int i = 0; i < meshCount; ++i) {
            meshes.add(new AssimpMesh(AIMesh.create(meshesBuffer.get(i))));
        }
        List<AssimpBone> allbones = meshes.stream().flatMap(mesh -> mesh.getBones().stream()).collect(Collectors.toList());
        var root = scene.mRootNode();
        var transforMatrix = AssimpHelper.generalizeNativeMatrix(root.mTransformation());
        var rootNode = AssimpNode.processNodesHierachy(root, null);
        animations = AssimpAnimation.processAnimations(scene, allbones, rootNode, transforMatrix);
        vaoid = GL30.glGenVertexArrays();
        animations.entrySet().stream().findFirst().ifPresentOrElse(entry -> currentAnimation = entry.getValue(), () -> currentAnimation = AssimpAnimation.buildDefaultAnimation(allbones.size()));
    }

    public void free() {
        aiReleaseImport(scene);
        scene = null;
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

    public void render(){
        //GLBufferFormats.POSITION_TEXTURE_NORMAL.bind();
        GL30.glBindVertexArray(vaoid);
        ShaderManager.INSTANCE.setUniform("useDirectUV",false);
        for (AssimpMesh mesh : meshes) {
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, mesh.getVertexBufferId());
            GL30.glVertexAttribPointer(0,3, GLDataType.FLOAT.glId, false,0,0);
            GL30.glEnableVertexAttribArray(0);
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, mesh.getTexCoordBufferId());
            GL30.glVertexAttribPointer(1, 2, GLDataType.FLOAT.glId, false, 0, 0);
            GL30.glEnableVertexAttribArray(1);
            if(mesh.getNormalBufferId() != 0){
                GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, mesh.getNormalBufferId());
                GL30.glVertexAttribPointer(3, 3, GLDataType.FLOAT.glId, false, 0, 0);
                GL30.glEnableVertexAttribArray(3);
            }
            if(mesh.getTangentBufferId() != 0){
                GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, mesh.getTangentBufferId());
                GL30.glVertexAttribPointer(4, 3, GLDataType.FLOAT.glId, false, 0, 0);
                GL30.glEnableVertexAttribArray(4);
            }
            if (mesh.getBoneIdBufferId() != 0) {
                GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, mesh.getBoneIdBufferId());
                GL30.glVertexAttribPointer(5, 4, GLDataType.INT.glId, false, 0, 0);
                GL30.glEnableVertexAttribArray(5);
            }
            if (mesh.getVertexWeightBufferId() != 0) {
                GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, mesh.getVertexWeightBufferId());
                GL30.glVertexAttribPointer(6, 4, GLDataType.FLOAT.glId, false, 0, 0);
                GL30.glEnableVertexAttribArray(6);
            }


            GL11.glEnable(GL11.GL_TEXTURE_2D);
            var mat = materials.get(mesh.getRawMesh().mMaterialIndex());
            mat.getEngineMaterial().bind("material");

            ShaderManager.INSTANCE.setUniform("u_Bones", currentAnimation.getCurrentFrame().getJointMatrices());

            GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, mesh.getElementArrayBufferId());
            GL30.glDrawElements(GL11.GL_TRIANGLES,mesh.getElementCount(),GLDataType.UNSIGNED_INT.glId,0);

        }

        GL30.glBindVertexArray(0);
    }
}
