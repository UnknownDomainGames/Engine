package engine.graphics.voxel;

import engine.graphics.texture.TextureAtlas;

public final class VoxelGraphics {

    private static TextureAtlas voxelTextureAtlas;

    public static TextureAtlas getVoxelTextureAtlas() {
        return voxelTextureAtlas;
    }

    static void setVoxelTextureAtlas(TextureAtlas textureAtlas) {
        voxelTextureAtlas = textureAtlas;
    }

    private VoxelGraphics() {
    }
}
