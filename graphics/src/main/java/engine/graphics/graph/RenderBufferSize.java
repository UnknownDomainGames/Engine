package engine.graphics.graph;

public interface RenderBufferSize {
    int getWidth();

    int getHeight();

    void resize(int width, int height);

    void resizeWithViewport(int viewportWidth, int viewportHeight);

    class FixedSize implements RenderBufferSize {
        private int width;
        private int height;

        public FixedSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public void resize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public void resizeWithViewport(int viewportWidth, int viewportHeight) {
            // DO NOT DO ANYTHING
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
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public void resize(int width, int height) {
            // DO NOT DO ANYTHING
        }

        @Override
        public void resizeWithViewport(int viewportWidth, int viewportHeight) {
            this.width = Math.round(viewportWidth * scaleWidth);
            this.height = Math.round(viewportHeight * scaleHeight);
        }
    }
}
