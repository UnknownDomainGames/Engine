package nullengine.client.rendering.display;

import nullengine.client.rendering.texture.Texture2DBuffer;
import org.lwjgl.glfw.GLFWImage;

public class WindowIcons {

    private GLFWImage.Buffer icons;

    public WindowIcons(Texture2DBuffer... icons){
        this.icons = GLFWImage.create(icons.length);
        for (int i = 0; i < icons.length; i++) {
            this.icons.get(i).set(icons[i].getWidth(),icons[i].getHeight(), icons[i].getBuffer());
        }
    }

    public GLFWImage.Buffer getIcons() {
        return icons;
    }
}
