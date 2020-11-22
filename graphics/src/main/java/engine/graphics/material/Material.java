package engine.graphics.material;

import engine.graphics.texture.Texture2D;
import engine.graphics.util.Struct;
import engine.util.Color;

import java.nio.ByteBuffer;

public final class Material implements Struct {
    private Color ambient = Color.fromGray(0.1f);
    private Color diffuse = Color.WHITE;
    private Color specular = Color.WHITE;
    private float reflectance;
    private Texture2D diffuseMap;
    private Texture2D specularMap;
    private Texture2D normalMap;
    private Texture2D alphaMap;

    public Color getAmbient() {
        return ambient;
    }

    public void setAmbient(Color ambient) {
        this.ambient = ambient;
    }

    public Color getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(Color diffuse) {
        this.diffuse = diffuse;
    }

    public Color getSpecular() {
        return specular;
    }

    public void setSpecular(Color specular) {
        this.specular = specular;
    }

    public float getReflectance() {
        return reflectance;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public Texture2D getDiffuseMap() {
        return diffuseMap;
    }

    public void setDiffuseMap(Texture2D diffuseMap) {
        this.diffuseMap = diffuseMap;
    }

    public Texture2D getSpecularMap() {
        return specularMap;
    }

    public void setSpecularMap(Texture2D specularMap) {
        this.specularMap = specularMap;
    }

    public Texture2D getNormalMap() {
        return normalMap;
    }

    public void setNormalMap(Texture2D normalMap) {
        this.normalMap = normalMap;
    }

    public Texture2D getAlphaMap() {
        return alphaMap;
    }

    public void setAlphaMap(Texture2D alphaMap) {
        this.alphaMap = alphaMap;
    }

    @Override
    public int sizeof() {
        return 68;
    }

    @Override
    public ByteBuffer get(int index, ByteBuffer buffer) {
        buffer.putInt(index, diffuseMap != null ? 1 : 0);
        buffer.putInt(index + 4, specularMap != null ? 1 : 0);
        buffer.putInt(index + 8, normalMap != null ? 1 : 0);
        buffer.putInt(index + 12, alphaMap != null ? 1 : 0);
        ambient.get(index + 16, buffer);
        diffuse.get(index + 32, buffer);
        specular.get(index + 48, buffer);
        buffer.putFloat(index + 64 - 4, reflectance);
        return buffer;
    }
}
