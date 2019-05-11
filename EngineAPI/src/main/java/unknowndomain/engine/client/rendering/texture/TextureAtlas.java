package unknowndomain.engine.client.rendering.texture;

import com.github.mouse0w0.lib4j.observable.value.ObservableValue;
import unknowndomain.engine.client.asset.AssetPath;

public interface TextureAtlas {

    TextureAtlasPart addTexture(AssetPath path);

    ObservableValue<GLTexture> getTexture();
}
