package unknowndomain.engine.client.rendering.model.assimp;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RegExUtils;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import unknowndomain.engine.Platform;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.rendering.light.Material;
import unknowndomain.engine.client.rendering.texture.GLTexture;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.regex.Pattern;

import static org.lwjgl.assimp.Assimp.*;

public class AssimpMaterial {
    private AIScene mScene;
    private AIMaterial mMaterial;

    private String name;

    private Material referenceMat;

    AssimpMaterial(AIScene scene, AIMaterial material, String filename) {

        mScene = scene;
        mMaterial = material;


        GLTexture diffuseTexture = loadTexture(aiTextureType_DIFFUSE, filename);
        GLTexture specularTexture = loadTexture(aiTextureType_SPECULAR, filename);
        GLTexture normalTexture = loadTexture(aiTextureType_NORMALS, filename);
        GLTexture alphaTexture = loadTexture(aiTextureType_OPACITY, filename);
        AIString mName = AIString.create();
        aiGetMaterialString(mMaterial, AI_MATKEY_NAME, aiTextureType_NONE, 0, mName);
        name = mName.dataString();
        AIColor4D mAmbientColor = AIColor4D.create();
        if (aiGetMaterialColor(mMaterial, AI_MATKEY_COLOR_AMBIENT,
                aiTextureType_NONE, 0, mAmbientColor) != 0) {
            throw new IllegalStateException(aiGetErrorString());
        }
        AIColor4D mDiffuseColor = AIColor4D.create();
        if (aiGetMaterialColor(mMaterial, AI_MATKEY_COLOR_DIFFUSE,
                aiTextureType_NONE, 0, mDiffuseColor) != 0) {
            throw new IllegalStateException(aiGetErrorString());
        }
        AIColor4D mSpecularColor = AIColor4D.create();
        if (aiGetMaterialColor(mMaterial, AI_MATKEY_COLOR_SPECULAR,
                aiTextureType_NONE, 0, mSpecularColor) != 0) {
            throw new IllegalStateException(aiGetErrorString());
        }
        FloatBuffer buf = BufferUtils.createFloatBuffer(1);
        IntBuffer ib = BufferUtils.createIntBuffer(1);
        ib.put(1);
        ib.flip();
        aiGetMaterialFloatArray(mMaterial, AI_MATKEY_SHININESS, aiTextureType_NONE, 0, buf, ib);
        referenceMat = new Material();
        referenceMat.setAmbientColor(new Vector3f(mAmbientColor.r(), mAmbientColor.g(), mAmbientColor.b()));
        referenceMat.setDiffuseColor(new Vector3f(mDiffuseColor.r(), mDiffuseColor.g(), mDiffuseColor.b()));
        referenceMat.setSpecularColor(new Vector3f(mSpecularColor.r(), mSpecularColor.g(), mSpecularColor.b()));
        referenceMat.setShininess(buf.get(0));
        referenceMat.setDiffuseUV(diffuseTexture);
        referenceMat.setSpecularUV(specularTexture);
        referenceMat.setNormalUV(normalTexture);
        referenceMat.setAlphaUV(alphaTexture);
    }

    private GLTexture loadTexture(int textureType, String filename) {
        AIString path = AIString.calloc();
        aiGetMaterialTexture(mMaterial, textureType, 0, path, null, null, null, null, null, (IntBuffer) null);
        String s = path.dataString();
        if (s.length() > 0) {
            if (s.matches("\\*\\d+")) {
                //This texture is embedded.
                PointerBuffer textures = mScene.mTextures();
                if (textures != null) {
                    var texture = AITexture.createSafe(textures.get(Integer.parseInt(Pattern.compile("\\*(\\d+)").matcher(s).group(1))));
                    var buf = texture.pcData(texture.mWidth() * texture.mHeight());
                    var buf1 = BufferUtils.createByteBuffer(texture.mWidth() * texture.mHeight() * 4);
                    for (AITexel aiTexel : buf) {
                        buf1.put(aiTexel.r()).put(aiTexel.g()).put(aiTexel.b()).put(aiTexel.a());
                    }
                    buf1.flip();
                    buf.free();
                    texture.free();
                    textures.free();
                    return GLTexture.of(texture.mWidth(), texture.mHeight(), buf1);
                }
            } else {
                //Promise: Loader now only search in texture/model/[FILENAME] folder
                return Platform.getEngineClient().getTextureManager().getTextureDirect(AssetPath.of("texture", "model", FilenameUtils.getBaseName(filename), s));
            }
        }
        return GLTexture.EMPTY;
    }

    Material getEngineMaterial() {
        return referenceMat;
    }

    public String getName() {
        return name;
    }
}
