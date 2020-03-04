package engine.graphics.shape;

import engine.client.asset.Asset;
import engine.graphics.Drawable;
import engine.graphics.mesh.MultiBufMesh;
import engine.graphics.texture.Texture2D;
import engine.graphics.util.BufferUtils;
import engine.graphics.vertex.VertexFormat;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static engine.client.asset.AssetTypes.TEXTURE;

public class SkyBox implements Drawable {

    private final Asset<Texture2D> texture;
    private final MultiBufMesh mesh;

    public SkyBox() {
        texture = Asset.create(TEXTURE, "engine", "misc/skybox");
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
        float[] floats = new float[24 * 4 * 4]; // 24 vertexes, 4 components, 4 bytes per component(float)
        Arrays.fill(floats, 1f); // fill color white(1f, 1f, 1f, 1f)
        ByteBuffer colors = BufferUtils.wrapAsByteBuffer(floats);
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
        mesh = MultiBufMesh.builder()
                .attribute(VertexFormat.POSITION, positions)
                .attribute(VertexFormat.COLOR_ALPHA, colors)
                .attribute(VertexFormat.TEX_COORD, texCoords)
                .indices(indices)
                .build();
    }

    @Override
    public void draw() {
        texture.get().bind();
        mesh.draw();
    }

    @Override
    public void dispose() {
        texture.dispose();
        mesh.dispose();
    }
}
