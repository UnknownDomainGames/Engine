package engine.graphics.graph;

import org.joml.Vector2i;
import org.joml.Vector2ic;

public interface RenderBufferSize {
    Vector2ic compute(int viewportWidth, int viewportHeight);

    class FixedSize implements RenderBufferSize {
        private final int width;
        private final int height;

        public FixedSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public Vector2ic compute(int viewportWidth, int viewportHeight) {
            return new Vector2i(width, height);
        }
    }

    class ViewportRelativeSize implements RenderBufferSize {
        private final float scaleWidth;
        private final float scaleHeight;

        private transient int width;
        private transient int height;

        public ViewportRelativeSize(float scaleWidth, float scaleHeight) {
            this.scaleWidth = scaleWidth;
            this.scaleHeight = scaleHeight;
        }

        @Override
        public Vector2ic compute(int viewportWidth, int viewportHeight) {
            return new Vector2i(Math.round(viewportWidth * scaleWidth), Math.round(viewportHeight * scaleHeight));
        }
    }
}
