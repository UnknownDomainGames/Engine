package engine.graphics.world;

import engine.client.asset.Asset;
import engine.client.asset.AssetURL;
import engine.graphics.RenderManager;
import engine.graphics.gl.texture.GLTexture2D;
import engine.graphics.mesh.GLMesh;
import engine.graphics.mesh.Mesh;
import org.lwjgl.opengl.GL11;

import static engine.client.asset.AssetTypes.TEXTURE;

public final class SkyboxRenderer {

    private Asset<GLTexture2D> skybox;
    private GLMesh skyboxMesh;

    public SkyboxRenderer(RenderManager manager) {
        skybox = manager.getEngine().getAssetManager().create(TEXTURE, AssetURL.of("engine", "misc/skybox"));
        skyboxMesh = GLMesh.of(new Mesh(
                new float[]{
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
                        -256, -256, 256, //Down
                },
                new float[]{
                        0.6666667f, 0.0f,
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
                        1.0f, 1.0f, //Down
                },
                new float[0],
                new int[]{
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

                },
                GL11.GL_TRIANGLES
        ));
    }

    public void render(float tpf) {
        skybox.get().bind();
        skyboxMesh.render();
    }
}
