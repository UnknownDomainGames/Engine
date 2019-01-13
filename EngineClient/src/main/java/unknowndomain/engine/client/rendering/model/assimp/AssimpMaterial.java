package unknowndomain.engine.client.rendering.model.assimp;

import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIString;
import unknowndomain.engine.client.rendering.texture.GLTexture;

import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import static org.lwjgl.assimp.Assimp.*;

public class AssimpMaterial {
    public AIMaterial mMaterial;
    public AIColor4D mAmbientColor;
    public AIColor4D mDiffuseColor;
    public AIColor4D mSpecularColor;

    private GLTexture diffuseTexture;

    public AssimpMaterial(AIMaterial material, String parentDir) {

        mMaterial = material;

        AIString path = AIString.calloc();

        aiGetMaterialTexture(material, aiTextureType_DIFFUSE, 0, path,null,null,null,null,null, (IntBuffer) null);

        String s = path.dataString();
        if (s != null && s.length() > 0) {
            try {
                InputStream stream = AssimpMaterial.class.getResourceAsStream("/"+parentDir + s);
                if(stream != null)
                    diffuseTexture = GLTexture.ofPNG(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
    }

    public GLTexture getDiffuseTexture() {
        return diffuseTexture;
    }
}
