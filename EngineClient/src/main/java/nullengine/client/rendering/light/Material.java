package nullengine.client.rendering.light;

import nullengine.client.rendering.shader.ShaderManager;
import nullengine.client.rendering.texture.GLTexture;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

//TODO to have a home his own (move to a specific package)
public class Material {
    Vector3f ambient = new Vector3f(0.1f);
    Vector3f diffuse = new Vector3f(1f);
    Vector3f specular = new Vector3f(1f);
    float shininess;
    GLTexture diffuseUV = GLTexture.EMPTY;
    GLTexture specularUV = GLTexture.EMPTY;
    GLTexture normalUV = GLTexture.EMPTY;
    GLTexture alphaUV = GLTexture.EMPTY;

    public void bind(String fieldName) {
        ShaderManager.setUniform(fieldName + ".ambient", ambient);
        ShaderManager.setUniform(fieldName + ".diffuseColor", diffuse);
        ShaderManager.setUniform(fieldName + ".specularColor", specular);
        ShaderManager.setUniform(fieldName + ".diffuse", 1);
        ShaderManager.setUniform(fieldName + ".specular", 2);
        ShaderManager.setUniform(fieldName + ".normalUV", 3);
        ShaderManager.setUniform(fieldName + ".alphaUV", 4);
        ShaderManager.setUniform(fieldName + ".shininess", shininess);
        if (diffuseUV != GLTexture.EMPTY) {
            ShaderManager.setUniform(fieldName + ".diffuseUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE1);
            diffuseUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        } else {
            ShaderManager.setUniform(fieldName + ".diffuseUseUV", false);
        }
        if (specularUV != GLTexture.EMPTY) {
            ShaderManager.setUniform(fieldName + ".specularUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE2);
            specularUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        } else {
            ShaderManager.setUniform(fieldName + ".specularUseUV", false);
        }
        if (normalUV != GLTexture.EMPTY) {
            ShaderManager.setUniform(fieldName + ".normalUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE3);
            normalUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        } else {
            ShaderManager.setUniform(fieldName + ".normalUseUV", false);
        }
        if (alphaUV != GLTexture.EMPTY) {
            ShaderManager.setUniform(fieldName + ".alphaUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE4);
            alphaUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        } else {
            ShaderManager.setUniform(fieldName + ".alphaUseUV", false);
        }
    }

    public Material setDiffuseColor(Vector3f diffuse) {
        this.diffuse = diffuse;
        return this;
    }

    public Material setAmbientColor(Vector3f ambient) {
        this.ambient = ambient;
        return this;
    }

    public Material setDiffuseUV(GLTexture diffuseUV) {
        this.diffuseUV = diffuseUV;
        return this;
    }

    public Material setSpecularUV(GLTexture specularUV) {
        this.specularUV = specularUV;
        return this;
    }

    public Material setNormalUV(GLTexture normalUV) {
        this.normalUV = normalUV;
        return this;
    }

    public Material setAlphaUV(GLTexture alphaUV) {
        this.alphaUV = alphaUV;
        return this;
    }

    public Material setSpecularColor(Vector3f specular) {
        this.specular = specular;
        return this;
    }

    public Material setShininess(float shininess) {
        this.shininess = shininess;
        return this;
    }
}
