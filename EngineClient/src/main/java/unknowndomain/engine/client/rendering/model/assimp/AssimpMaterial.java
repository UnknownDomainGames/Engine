package unknowndomain.engine.client.rendering.model.assimp;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIString;
import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.client.UnknownDomain;
import unknowndomain.engine.client.rendering.light.Material;
import unknowndomain.engine.client.rendering.texture.GLTexture;
import unknowndomain.engine.client.resource.ResourcePath;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.assimp.Assimp.*;

public class AssimpMaterial {
    public AIMaterial mMaterial;
    public AIColor4D mAmbientColor;
    public AIColor4D mDiffuseColor;
    public AIColor4D mSpecularColor;

    private GLTexture diffuseTexture;
    private GLTexture specularTexture;
    private GLTexture normalTexture;
    private GLTexture alphaTexture;

    private Material referenceMat;

    public AssimpMaterial(AIMaterial material, String parentDir) {

        mMaterial = material;

        diffuseTexture = loadTexture(aiTextureType_DIFFUSE, parentDir);
        specularTexture = loadTexture(aiTextureType_SPECULAR, parentDir);
        normalTexture = loadTexture(aiTextureType_NORMALS, parentDir);
        alphaTexture = loadTexture(aiTextureType_OPACITY, parentDir);

        mAmbientColor = AIColor4D.create();
        if (aiGetMaterialColor(mMaterial, AI_MATKEY_COLOR_AMBIENT,
                aiTextureType_NONE, 0, mAmbientColor) != 0) {
            throw new IllegalStateException(aiGetErrorString());
        }
        mDiffuseColor = AIColor4D.create();
        if (aiGetMaterialColor(mMaterial, AI_MATKEY_COLOR_DIFFUSE,
                aiTextureType_NONE, 0, mDiffuseColor) != 0) {
            throw new IllegalStateException(aiGetErrorString());
        }
        mSpecularColor = AIColor4D.create();
        if (aiGetMaterialColor(mMaterial, AI_MATKEY_COLOR_SPECULAR,
                aiTextureType_NONE, 0, mSpecularColor) != 0) {
            throw new IllegalStateException(aiGetErrorString());
        }
        FloatBuffer buf = BufferUtils.createFloatBuffer(1);
        IntBuffer ib = BufferUtils.createIntBuffer(1);
        ib.put(1);
        ib.flip();
        aiGetMaterialFloatArray(mMaterial,AI_MATKEY_SHININESS, aiTextureType_NONE,0,buf, ib);
        referenceMat = new Material();
        referenceMat.setAmbientColor(new Vector3f(mAmbientColor.r(),mAmbientColor.g(),mAmbientColor.b()));
        referenceMat.setDiffuseColor(new Vector3f(mDiffuseColor.r(),mDiffuseColor.g(),mDiffuseColor.b()));
        referenceMat.setSpecularColor(new Vector3f(mSpecularColor.r(),mSpecularColor.g(),mSpecularColor.b()));
        referenceMat.setShininess(buf.get(0));
        if(diffuseTexture != null){
            referenceMat.setDiffuseUV(diffuseTexture);
        }
        if(specularTexture != null){
            referenceMat.setSpecularUV(specularTexture);
        }
        if(normalTexture != null){
            referenceMat.setNormalUV(normalTexture);
        }
        if(alphaTexture != null){
            referenceMat.setAlphaUV(alphaTexture);
        }
    }

    public GLTexture loadTexture(int textureType, String parentDir) {
        AIString path = AIString.calloc();

        aiGetMaterialTexture(mMaterial, textureType, 0, path,null,null,null,null,null, (IntBuffer) null);
        String s = path.dataString();
        if (s.length() > 0) {
                return UnknownDomain.getEngine().getTextureManager().getTexture(new ResourcePath("texture", "/"+parentDir + s));
        }
        return null;
    }

    public GLTexture getDiffuseTexture() {
        return diffuseTexture;
    }

    public Material getEngineMaterial() {
        return referenceMat;
    }
}
