package unknowndomain.engine.client.rendering.light;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;
import unknowndomain.engine.client.rendering.texture.GLTexture;

//TODO to have a home his own (move to a specific package)
public class Material {
    Vector3f ambient = new Vector3f(0.1f);
    Vector3f diffuse = new Vector3f(1f);
    Vector3f specular = new Vector3f(1f);
    float shininess;
    GLTexture diffuseUV;
    GLTexture specularUV;
    GLTexture normalUV;
    GLTexture alphaUV;
    public void bind(ShaderProgram program, String fieldName){
        program.setUniform(fieldName + ".ambient", ambient);
        program.setUniform(fieldName + ".diffuseColor", diffuse);
        program.setUniform(fieldName + ".specularColor", specular);
        program.setUniform(fieldName + ".diffuse", 1);
        program.setUniform(fieldName + ".specular", 2);
        program.setUniform(fieldName + ".normalUV", 3);
        program.setUniform(fieldName + ".alphaUV", 4);
        program.setUniform(fieldName + ".shininess", shininess);
        if(diffuseUV != null){
            program.setUniform(fieldName + ".diffuseUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE1);
            diffuseUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        }else{
            program.setUniform(fieldName + ".diffuseUseUV", false);
        }
        if(specularUV != null){
            program.setUniform(fieldName + ".specularUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE2);
            specularUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        }else{
            program.setUniform(fieldName + ".specularUseUV", false);
        }
        if(normalUV != null){
            program.setUniform(fieldName + ".normalUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE3);
            normalUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        }else{
            program.setUniform(fieldName + ".normalUseUV", false);
        }
        if(alphaUV != null){
            program.setUniform(fieldName + ".alphaUseUV", true);
            GL15.glActiveTexture(GL15.GL_TEXTURE4);
            alphaUV.bind();
            GL15.glActiveTexture(GL13.GL_TEXTURE0);
        }else{
            program.setUniform(fieldName + ".alphaUseUV", false);
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
