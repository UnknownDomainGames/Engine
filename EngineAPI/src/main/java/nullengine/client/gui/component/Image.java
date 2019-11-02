package nullengine.client.gui.component;

import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.asset.AssetURL;

public class Image extends Control {
    private ImageView imageView = new ImageView();

    public Image() {
        this.getChildren().addAll(imageView);
    }

    public SimpleMutableFloatValue imageX() {
        return imageView.imageX();
    }

    public SimpleMutableFloatValue imageY() {
        return imageView.imageY();
    }

    public SimpleMutableFloatValue imageWidth() {
        return imageView.imageWidth();
    }

    public SimpleMutableFloatValue imageHeight() {
        return imageView.imageHeight();
    }

    public SimpleMutableObjectValue<AssetURL> path() {
        return imageView.image();
    }

    @Override
    public float computeWidth() {
        return imageView.prefWidth();
    }

    @Override
    public float computeHeight() {
        return imageView.prefHeight();
    }
}
