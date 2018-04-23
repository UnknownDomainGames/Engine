package com.github.unknownstudio.unknowndomain.engine.client.render;

import org.lwjgl.opengl.GL11;

import com.github.unknownstudio.unknowndomain.engine.client.resource.Texture2D;

import java.util.Arrays;

public class RenderSkyBox extends Render {

    public final Texture2D[] textures;

    /**
     *
     * @param texes textures of skybox, order: E-S-W-N-U-D
     */
    public RenderSkyBox(Texture2D[] texes){
        if (texes.length < 6) throw new IllegalArgumentException();
        else if(texes.length > 6) texes = Arrays.copyOf(texes, 6);
        textures = texes;
    }

    public void render(){
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        for (int i = 0; i < 6; i++) {
            GL11.glPushMatrix();
            switch (i){
                case 0:
                    break;
                case 1:
                    GL11.glRotated(90, 0,1,0);
                case 2:
                    GL11.glRotated(180, 0,1,0);
                case 3:
                    GL11.glRotated(270, 0,1,0);
                case 4:
                    GL11.glRotated(90, 0,0,1);
                case 5:
                    GL11.glRotated(-90, 0,0,1);
            }
            GL11.glColor4d(1.0,1.0,1.0,1.0);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2d(0,0);
            GL11.glVertex3d(1.0, -1.0, -1.0);
            GL11.glTexCoord2d(1,0);
            GL11.glVertex3d(1.0, -1.0, 1.0);
            GL11.glTexCoord2d(1,1);
            GL11.glVertex3d(1.0, 1.0, 1.0);
            GL11.glTexCoord2d(0,1);
            GL11.glVertex3d(1.0, 1.0, -1.0);
            GL11.glEnd();
            GL11.glPopMatrix();
        }
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }
}
