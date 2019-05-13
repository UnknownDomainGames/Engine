package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import unknowndomain.engine.client.asset.AssetPath;

public class Image extends Control{
    private TextureImg textureImg = new TextureImg();
    public Image(){
        this.getChildren().addAll(textureImg);
    }

    public SimpleMutableFloatValue imageX() {
        return textureImg.imageX();
    }

    public SimpleMutableFloatValue imageY() {
        return textureImg.imageY();
    }

    public SimpleMutableFloatValue imageWidth() {
        return textureImg.imageWidth();
    }

    public SimpleMutableFloatValue imageHeight() {
        return textureImg.imageHeight();
    }

    public SimpleMutableObjectValue<AssetPath> path() {
        return textureImg.path();
    }

    @Override
    public float prefWidth() {
        return textureImg.prefWidth();
    }

    @Override
    public float prefHeight() {
        return textureImg.prefHeight();
    }
}
