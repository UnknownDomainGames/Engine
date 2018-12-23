package unknowndomain.engine.client.rendering.texture;

public interface TextureUV {

    TextureUV DEFAULT_TEXTURE_UV = of(0,0,1,1);

    float getMinU();

    float getMinV();

    float getMaxU();

    float getMaxV();

    static TextureUV of(float minU, float minV, float maxU, float maxV) {
        return new TextureUV() {
            @Override
            public float getMinU() {
                return minU;
            }

            @Override
            public float getMinV() {
                return minV;
            }

            @Override
            public float getMaxU() {
                return maxU;
            }

            @Override
            public float getMaxV() {
                return maxV;
            }
        };
    }

    static TextureUV of(int texWidth, int texHeight, int offsetX, int offsetY, int width, int height) {
        return of((float) offsetX / texWidth,
                (float) offsetY / texHeight,
                (float) (offsetX + width) / texWidth,
                (float) (offsetY + height) / texHeight);
    }
}
