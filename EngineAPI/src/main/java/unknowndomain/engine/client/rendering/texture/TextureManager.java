package unknowndomain.engine.client.rendering.texture;

import com.github.mouse0w0.lib4j.observable.value.ObservableValue;
import unknowndomain.engine.client.asset.AssetPath;

public interface TextureManager {

    ObservableValue<GLTexture> getTexture(AssetPath path);

    GLTexture getTextureDirect(AssetPath path);

    TextureUV addTextureToAtlas(AssetPath path, TextureType type);

    ObservableValue<GLTexture> getTextureAtlas(TextureType type);

    void reloadTextureAtlas(TextureType type);
}
