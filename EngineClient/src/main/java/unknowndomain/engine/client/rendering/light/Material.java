package unknowndomain.engine.client.rendering.light;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import unknowndomain.engine.client.rendering.shader.ShaderManager;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;
import unknowndomain.engine.client.rendering.texture.GLTexture;

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
    public void bind(String fieldName){
        ShaderManager.INSTANCE.setUniform(fieldName + ".ambient", ambient);
        ShaderManager.INSTANCE.setUniform(fieldName + ".diffuseColor", diffuse);
        ShaderManager.INSTANCE.setUniform(fieldName + ".specularColor", specular);
        ShaderManager.INSTANCE.setUniform(fieldName + ".diffuse", 1);
        ShaderManager.INSTANCE.setUniform(fieldName + ".specular", 2);
        ShaderManager.INSTANCE.setUniform(fieldName + ".normalUV", 3);
        ShaderManager.INSTANCE.setUniform(fieldName + ".alphaUV", 4);
        ShaderManager.INSTANCE.setUniform(fieldName + ".shininess", shininess);
        if(diffuseUV != GLTexture.EMPTY){
            ShaderManager.INSTANCE.setUniform(fieldName + ".diffuseUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE1);
            diffuseUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        }else{
            ShaderManager.INSTANCE.setUniform(fieldName + ".diffuseUseUV", false);
        }
        if(specularUV != GLTexture.EMPTY){
            ShaderManager.INSTANCE.setUniform(fieldName + ".specularUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE2);
            specularUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        }else{
            ShaderManager.INSTANCE.setUniform(fieldName + ".specularUseUV", false);
        }
        if(normalUV != GLTexture.EMPTY){
            ShaderManager.INSTANCE.setUniform(fieldName + ".normalUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE3);
            normalUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        }else{
            ShaderManager.INSTANCE.setUniform(fieldName + ".normalUseUV", false);
        }
        if(alphaUV != GLTexture.EMPTY){
            ShaderManager.INSTANCE.setUniform(fieldName + ".alphaUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE4);
            alphaUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        }else{
            ShaderManager.INSTANCE.setUniform(fieldName + ".alphaUseUV", false);
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
