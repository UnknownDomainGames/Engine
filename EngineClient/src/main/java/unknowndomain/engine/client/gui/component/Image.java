package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.client.UnknownDomain;
import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.rendering.ComponentRenderer;
import unknowndomain.engine.client.rendering.texture.GLTexture;
import unknowndomain.engine.client.rendering.texture.TextureManagerImpl;
import unknowndomain.engine.client.rendering.texture.TextureType;
import unknowndomain.engine.client.resource.ResourcePath;

public class Image extends Component {
    private final SimpleMutableObjectValue<ResourcePath> image = new SimpleMutableObjectValue<>();
    private final SimpleMutableObjectValue<TextureType> type = new SimpleMutableObjectValue<>(); //WE SHOULD ALLOW ANY TEXTURE //FIXME weird value. BLAME THE DESIGNER OF TEXTUREMANAGERIMPL
    private ResourcePath cachedPath;
    private GLTexture cachedTexture;

    public Image(){
        image.addChangeListener((ob,o,n)->{
            buildCache();
            requestParentLayout();
        });
        type.addChangeListener((ob,o,n)->requestParentLayout());
    }

    public void buildCache(){
        //TODO grab GLTexture into cachedTexture. If TextureManager does not contain texture assigned with path, replace with NULL
//        var tt = UnknownDomain.getEngine().getTextureManager().getTextureAtlas()
//        if(){
//
//        }
    }

    @Override
    public float prefWidth() {
        return cachedTexture != null ? cachedTexture.getWidth() : 0;
    }

    @Override
    public float prefHeight() {
        return cachedTexture != null ? cachedTexture.getHeight() : 0;
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return null;
    }

    public GLTexture getCachedTexture() {
        return cachedTexture;
    }
}
