package nullengine.client.rendering.texture;

import com.github.mouse0w0.observable.value.ObservableValue;
import nullengine.client.asset.AssetURL;

public interface TextureAtlas {

    TextureAtlasPart addTexture(AssetURL url);

    ObservableValue<GLTexture> getTexture();
}
