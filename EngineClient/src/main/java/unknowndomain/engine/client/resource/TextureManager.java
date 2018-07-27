package unknowndomain.engine.client.resource;

import unknowndomain.engine.api.util.DomainedPath;

import java.util.HashMap;
import java.util.Map;

public class TextureManager {
    private Map<DomainedPath, Texture2D> textures;

    public TextureManager(){
        textures = new HashMap<>();
    }

    public void registerTexture(DomainedPath path, Texture2D texture){

        textures.put(path, texture);
    }
}
