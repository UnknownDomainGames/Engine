package unknowndomain.engine.client.texture;

import unknowndomain.engine.api.util.DomainedPath;

import java.util.HashMap;
import java.util.Map;

public class TextureMap extends GLTexture {
    private Map<DomainedPath, int[]> uvMapping = new HashMap<>();
    private int width, height;

    private int x, y;

    public TextureMap(int id) {
        super(id);
    }

    public int[] require(int width, int height) {
        if (this.x + width < this.width) {

        }
        return new int[0];
    }


}
