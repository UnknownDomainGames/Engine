package unknowndomain.engine.client.model;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

public class ModelAssimpImported {
    private AIScene scene;
    private List<AIMesh> meshes;
    private List<AIMaterial> materials;
    private List<AITexture> textures;
    private List<AIAnimation> animations;

    public ModelAssimpImported(AIScene scene){
        this.scene = scene;

        int meshs = scene.mNumMeshes();
        PointerBuffer meshBuf = scene.mMeshes();
        meshes = new ArrayList<>();
        for (int i = 0; i < meshs; i++){
            meshes.add(AIMesh.create(meshBuf.get(i)));
        }

        int materials = scene.mNumMaterials();
        PointerBuffer materialBuf = scene.mMaterials();
        this.materials = new ArrayList<>();
        for(int i = 0; i < materials;i++){
            this.materials.add(AIMaterial.create(materialBuf.get(i)));
        }

        PointerBuffer texBuf = scene.mTextures();
        if(texBuf != null){
            int textures = scene.mNumTextures();
            this.textures = new ArrayList<>();
            for (int i = 0;i<textures;i++){
                this.textures.add(AITexture.create(texBuf.get(i)));
            }
        }
        PointerBuffer aniBuf = scene.mAnimations();
        if(aniBuf != null){
            int animations = scene.mNumAnimations();
            this.animations = new ArrayList<>();
            for (int i = 0; i < animations;i++){
                this.animations.add(AIAnimation.create(aniBuf.get(i)));
            }
        }
    }

    public void dispose(){
        aiReleaseImport(scene);
        scene = null;
        meshes.clear();
        materials.clear();
        if(textures != null)textures.clear();
        if(animations != null)animations.clear();
    }
}
