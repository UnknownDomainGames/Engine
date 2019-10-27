package nullengine.client.rendering.material;

import nullengine.client.rendering.gl.texture.GLTexture2D;
import nullengine.client.rendering.shader.ShaderManager;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

//TODO to have a home his own (move to a specific package)
public class Material {
    private Vector3f ambient = new Vector3f(0.1f);
    private Vector3f diffuse = new Vector3f(1f);
    private Vector3f specular = new Vector3f(1f);
    private float shininess;
    private GLTexture2D diffuseUV = GLTexture2D.EMPTY;
    private GLTexture2D specularUV = GLTexture2D.EMPTY;
    private GLTexture2D normalUV = GLTexture2D.EMPTY;
    private GLTexture2D alphaUV = GLTexture2D.EMPTY;

    public void bind(String fieldName) {
        ShaderManager shaderManager = ShaderManager.instance();
        shaderManager.setUniform(fieldName + ".ambient", ambient);
        shaderManager.setUniform(fieldName + ".diffuseColor", diffuse);
        shaderManager.setUniform(fieldName + ".specularColor", specular);
        shaderManager.setUniform(fieldName + ".diffuse", 1);
        shaderManager.setUniform(fieldName + ".specular", 2);
        shaderManager.setUniform(fieldName + ".normalUV", 3);
        shaderManager.setUniform(fieldName + ".alphaUV", 4);
        shaderManager.setUniform(fieldName + ".shininess", shininess);
        if (diffuseUV != GLTexture2D.EMPTY) {
            shaderManager.setUniform(fieldName + ".diffuseUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE1);
            diffuseUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        } else {
            shaderManager.setUniform(fieldName + ".diffuseUseUV", false);
        }
        if (specularUV != GLTexture2D.EMPTY) {
            shaderManager.setUniform(fieldName + ".specularUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE2);
            specularUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        } else {
            shaderManager.setUniform(fieldName + ".specularUseUV", false);
        }
        if (normalUV != GLTexture2D.EMPTY) {
            shaderManager.setUniform(fieldName + ".normalUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE3);
            normalUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        } else {
            shaderManager.setUniform(fieldName + ".normalUseUV", false);
        }
        if (alphaUV != GLTexture2D.EMPTY) {
            shaderManager.setUniform(fieldName + ".alphaUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE4);
            alphaUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        } else {
            shaderManager.setUniform(fieldName + ".alphaUseUV", false);
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

    public Material setDiffuseUV(GLTexture2D diffuseUV) {
        this.diffuseUV = diffuseUV;
        return this;
    }

    public Material setSpecularUV(GLTexture2D specularUV) {
        this.specularUV = specularUV;
        return this;
    }

    public Material setNormalUV(GLTexture2D normalUV) {
        this.normalUV = normalUV;
        return this;
    }

    public Material setAlphaUV(GLTexture2D alphaUV) {
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
