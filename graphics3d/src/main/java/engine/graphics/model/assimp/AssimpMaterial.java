package engine.graphics.model.assimp;

import engine.graphics.GraphicsEngine;
import engine.graphics.image.BufferedImage;
import engine.graphics.material.Material;
import engine.graphics.texture.ColorFormat;
import engine.graphics.texture.Texture2D;
import org.apache.commons.io.FilenameUtils;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.lwjgl.assimp.Assimp.*;

public class AssimpMaterial {
    private AIScene mScene;
    private AIMaterial mMaterial;

    private String name;

    private Material referenceMat;

    AssimpMaterial(AIScene scene, AIMaterial material, String url) {

        mScene = scene;
        mMaterial = material;


        Texture2D diffuseTexture = loadTexture(aiTextureType_DIFFUSE, url);
        Texture2D specularTexture = loadTexture(aiTextureType_SPECULAR, url);
        Texture2D normalTexture = loadTexture(aiTextureType_NORMALS, url);
        Texture2D alphaTexture = loadTexture(aiTextureType_OPACITY, url);
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

    private Texture2D loadTexture(int textureType, String url) {
        AIString path = AIString.calloc();
        aiGetMaterialTexture(mMaterial, textureType, 0, path, null, null, null, null, null, (IntBuffer) null);
        String s = path.dataString();
        if (s.length() > 0) {
            if (s.matches("\\*\\d+")) {
                //This texture is embedded.
                PointerBuffer textures = mScene.mTextures();
                if (textures != null) {
                    var texture = AITexture.createSafe(textures.get(Integer.parseInt(Pattern.compile("\\*(\\d+)").matcher(s).group(1))));
                    var width = texture.mWidth();
                    var height = texture.mHeight();
                    var buf = texture.pcData(width * height);
                    var buf1 = BufferUtils.createByteBuffer(width * height * 4);
                    for (AITexel aiTexel : buf) {
                        buf1.put(aiTexel.r()).put(aiTexel.g()).put(aiTexel.b()).put(aiTexel.a());
                    }
                    buf1.flip();
                    buf.free();
                    texture.free();
                    textures.free();
                    return GraphicsEngine.getGraphicsBackend().getResourceFactory().createTexture2DBuilder().format(ColorFormat.RGBA8).build(buf1, width, height);
                }
            } else {
                //Promise: Loader now only search in texture/model/[FILENAME] folder
                try (var channel = Files.newByteChannel(Path.of("texture", "model", FilenameUtils.getBaseName(url), s))) {
                    var buffer = ByteBuffer.allocateDirect(Math.toIntExact(channel.size()));
                    channel.read(buffer);
                    buffer.flip();
                    var builder = GraphicsEngine.getGraphicsBackend().getResourceFactory().createTexture2DBuilder();
                    return builder.build(BufferedImage.load(buffer));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

    Material getEngineMaterial() {
        return referenceMat;
    }

    public String getName() {
        return name;
    }

    void assignName(String name){
        this.name = name;
    }
}
