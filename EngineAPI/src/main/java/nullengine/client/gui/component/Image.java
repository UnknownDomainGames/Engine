package nullengine.client.gui.component;

import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.asset.AssetURL;

public class Image extends Control {
    private TextureImg textureImg = new TextureImg();

    public Image() {
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

    public SimpleMutableObjectValue<AssetURL> path() {
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
