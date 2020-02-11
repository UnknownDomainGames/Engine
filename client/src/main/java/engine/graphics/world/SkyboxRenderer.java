package engine.graphics.world;

import engine.client.asset.Asset;
import engine.client.asset.AssetURL;
import engine.graphics.RenderManager;
import engine.graphics.mesh.Mesh;
import engine.graphics.texture.Texture2D;
import engine.graphics.util.BufferUtils;
import engine.graphics.vertex.VertexFormat;

import java.nio.ByteBuffer;

import static engine.client.asset.AssetTypes.TEXTURE;

public final class SkyboxRenderer {

    private Asset<Texture2D> skybox;
    private Mesh mesh;

    public SkyboxRenderer(RenderManager manager) {
        skybox = manager.getEngine().getAssetManager().create(TEXTURE, AssetURL.of("engine", "misc/skybox"));
        ByteBuffer positions = BufferUtils.wrapAsByteBuffer(new float[]{
                256, 256, -256,
                256, 256, 256,
                256, -256, -256,
                256, -256, 256, //East
                -256, 256, -256,
                -256, 256, 256,
                -256, -256, -256,
                -256, -256, 256, //West
                -256, 256, -256,
                256, 256, -256,
                -256, -256, -256,
                256, -256, -256, //North
                256, 256, 256,
                -256, 256, 256,
                256, -256, 256,
                -256, -256, 256, //South
                256, 256, -256,
                256, 256, 256,
                -256, 256, -256,
                -256, 256, 256, //Up
                256, -256, -256,
                256, -256, 256,
                -256, -256, -256,
                -256, -256, 256 //Down
        });
        ByteBuffer texCoords = BufferUtils.wrapAsByteBuffer(0.6666667f, 0.0f,
                1.0f, 0.0f,
                0.6666667f, 0.5f,
                1.0f, 0.5f, //East
                0.33333334f, 0.0f,
                0.0f, 0.0f,
                0.33333334f, 0.5f,
                0.0f, 0.5f, //West
                0.33333334f, 0.0f,
                0.6666667f, 0.0f,
                0.33333334f, 0.5f,
                0.6666667f, 0.5f, //North
                0.0f, 0.5f,
                0.33333334f, 0.5f,
                0.0f, 1.0f,
                0.33333334f, 1.0f, //South
                0.33333334f, 1.0f,
                0.6666667f, 1.0f,
                0.33333334f, 0.5f,
                0.6666667f, 0.5f, //Up
                0.6666667f, 0.5f,
                1.0f, 0.5f,
                0.6666667f, 1.0f,
                1.0f, 1.0f //Down
        );
        ByteBuffer indices = BufferUtils.wrapAsByteBuffer(new byte[]{
                0, 2, 1,
                2, 3, 1, //East
                4, 5, 6,
                6, 5, 7, //West
                8, 10, 9,
                10, 11, 9, //North
                12, 14, 13,
                13, 14, 15, //South
                16, 17, 18,
                18, 17, 19, //Up
                20, 22, 21,
                21, 22, 23 //Down

        });
        mesh = Mesh.builder()
                .attribute(VertexFormat.POSITION, positions)
                .attribute(VertexFormat.COLOR_ALPHA, null)
                .attribute(VertexFormat.TEX_COORD, texCoords)
                .indices(indices)
                .build();
    }

    public void render(float tpf) {
        skybox.get().bind();
        mesh.draw();
    }
}
