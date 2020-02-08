package engine.graphics.gl.texture;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_1D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_3D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_1D_ARRAY;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_BUFFER;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_RECTANGLE;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE_ARRAY;
import static org.lwjgl.opengl.GL40.GL_TEXTURE_CUBE_MAP_ARRAY;

public enum GLTextureType {
    TEX_1D(GL_TEXTURE_1D),
    TEX_2D(GL_TEXTURE_2D),
    TEX_2D_MULTI_SAMPLE(GL_TEXTURE_2D_MULTISAMPLE),
    TEX_3D(GL_TEXTURE_3D),
    TEX_CUBE_MAP(GL_TEXTURE_CUBE_MAP),
    TEX_1D_ARRAY(GL_TEXTURE_1D_ARRAY),
    TEX_2D_ARRAY(GL_TEXTURE_2D_ARRAY),
    TEX_2D_MULTI_SAMPLE_ARRAY(GL_TEXTURE_2D_MULTISAMPLE_ARRAY),
    TEX_CUBE_MAP_ARRAY(GL_TEXTURE_CUBE_MAP_ARRAY),
    TEX_RECTANGLE(GL_TEXTURE_RECTANGLE),
    TEX_BUFFER(GL_TEXTURE_BUFFER);

    public final int gl;

    GLTextureType(int gl) {
        this.gl = gl;
    }
}
