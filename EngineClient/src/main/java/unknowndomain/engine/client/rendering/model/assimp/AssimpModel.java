package unknowndomain.engine.client.rendering.model.assimp;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import unknowndomain.engine.Engine;
import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.client.UnknownDomain;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;
import unknowndomain.engine.client.rendering.util.GLDataType;
import unknowndomain.engine.client.rendering.util.GLHelper;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferElements;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferFormats;

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
        vaoid = GL30.glGenVertexArrays();
    }

    public void free() {
        aiReleaseImport(scene);
        scene = null;
        meshes = null;
        materials = null;
    }

    public void render(ShaderProgram program){
        //GLBufferFormats.POSITION_TEXTURE_NORMAL.bind();
        GL30.glBindVertexArray(vaoid);
        program.setUniform("useDirectUV",false);
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
            mat.getEngineMaterial().bind(program, "material");
//            if(mat.getDiffuseTexture() != null){
//                mat.getDiffuseTexture().bind();
//            }

            GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, mesh.getElementArrayBufferId());
            GL30.glDrawElements(GL11.GL_TRIANGLES,mesh.getElementCount(),GLDataType.UNSIGNED_INT.glId,0);

        }

        GL30.glBindVertexArray(0);
    }
}
