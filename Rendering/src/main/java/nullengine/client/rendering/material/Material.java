package nullengine.client.rendering.material;

import nullengine.client.rendering.gl.texture.GLTexture2D;
import nullengine.client.rendering.scene.BindingProxy;
import nullengine.client.rendering.texture.Texture2D;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;

//TODO to have a home his own (move to a specific package)
public class Material {
    private Vector3f ambient = new Vector3f(0.1f);
    private Vector3f diffuse = new Vector3f(1f);
    private Vector3f specular = new Vector3f(1f);
    private float shininess;
    private Texture2D diffuseUV;
    private Texture2D specularUV;
    private Texture2D normalUV;
    private Texture2D alphaUV;

    public void bind(BindingProxy proxy, String fieldName) {
        proxy.setUniform(fieldName + ".ambient", ambient);
        proxy.setUniform(fieldName + ".diffuseColor", diffuse);
        proxy.setUniform(fieldName + ".specularColor", specular);
        proxy.setUniform(fieldName + ".diffuse", 1);
        proxy.setUniform(fieldName + ".specular", 2);
        proxy.setUniform(fieldName + ".normalUV", 3);
        proxy.setUniform(fieldName + ".alphaUV", 4);
        proxy.setUniform(fieldName + ".shininess", shininess);
        if (diffuseUV != null) {
            proxy.setUniform(fieldName + ".diffuseUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE1);
            diffuseUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        } else {
            proxy.setUniform(fieldName + ".diffuseUseUV", false);
        }
        if (specularUV != null) {
            proxy.setUniform(fieldName + ".specularUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE2);
            specularUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        } else {
            proxy.setUniform(fieldName + ".specularUseUV", false);
        }
        if (normalUV != null) {
            proxy.setUniform(fieldName + ".normalUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE3);
            normalUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        } else {
            proxy.setUniform(fieldName + ".normalUseUV", false);
        }
        if (alphaUV != null) {
            proxy.setUniform(fieldName + ".alphaUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE4);
            alphaUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        } else {
            proxy.setUniform(fieldName + ".alphaUseUV", false);
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
