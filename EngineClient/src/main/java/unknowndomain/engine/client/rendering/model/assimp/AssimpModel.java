package unknowndomain.engine.client.rendering.model.assimp;

import org.joml.Matrix4f;
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

import static org.lwjgl.assimp.Assimp.aiReleaseImport;

public class AssimpModel {
    private AIScene scene;
    private List<AssimpMesh> meshes;
    private List<AssimpMaterial> materials;

    private int vaoid;

    public AssimpModel(AIScene scene, String parentDir){
        this.scene = scene;
        int meshCount = scene.mNumMeshes();
        PointerBuffer meshesBuffer = scene.mMeshes();
        meshes = new ArrayList<>();
        for (int i = 0; i < meshCount; ++i) {
            meshes.add(new AssimpMesh(AIMesh.create(meshesBuffer.get(i))));
        }
        materials = new ArrayList<>();
        int materialCount = scene.mNumMaterials();
        var materialBuf = scene.mMaterials();
        for (int i = 0; i < materialCount; i++) {
            materials.add(new AssimpMaterial(AIMaterial.create(materialBuf.get(i)), parentDir));
        }
        var root = scene.mRootNode();
        var transforMatrix = new Matrix4f();
        root.mTransformation(aiMatrix4x4 -> transforMatrix.m00(aiMatrix4x4.a1()).m01(aiMatrix4x4.a2()).m02(aiMatrix4x4.a3()).m03(aiMatrix4x4.a4())
                .m10(aiMatrix4x4.b1()).m11(aiMatrix4x4.b2()).m12(aiMatrix4x4.b3()).m13(aiMatrix4x4.b4())
                .m20(aiMatrix4x4.c1()).m21(aiMatrix4x4.c2()).m22(aiMatrix4x4.c3()).m23(aiMatrix4x4.c4())
                .m30(aiMatrix4x4.d1()).m31(aiMatrix4x4.d2()).m32(aiMatrix4x4.d3()).m33(aiMatrix4x4.d4()));

        vaoid = GL30.glGenVertexArrays();
    }

    public void free() {
        aiReleaseImport(scene);
        scene = null;
        meshes = null;
        materials = null;
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
            GL30.glVertexAttribPointer(2,2, GLDataType.FLOAT.glId, false,0,0);
            GL30.glEnableVertexAttribArray(2);
            if(mesh.getNormalBufferId() != 0){
                GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, mesh.getNormalBufferId());
                GL30.glVertexAttribPointer(3,3, GLDataType.FLOAT.glId, false,0,0);
                GL30.glEnableVertexAttribArray(3);
            }
            if(mesh.getTangentBufferId() != 0){
                GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, mesh.getTangentBufferId());
                GL30.glVertexAttribPointer(4,3, GLDataType.FLOAT.glId, false,0,0);
                GL30.glEnableVertexAttribArray(4);
            }

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            var mat = materials.get(mesh.getRawMesh().mMaterialIndex());
            mat.getEngineMaterial().bind(ShaderManager.INSTANCE.getUsingShader(), "material");
//            if(mat.getDiffuseTexture() != null){
//                mat.getDiffuseTexture().bind();
//            }

            GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, mesh.getElementArrayBufferId());
            GL30.glDrawElements(GL11.GL_TRIANGLES,mesh.getElementCount(),GLDataType.UNSIGNED_INT.glId,0);

        }

        GL30.glBindVertexArray(0);
    }
}
