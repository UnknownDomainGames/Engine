package unknowndomain.engine.client.rendering.block;

public enum Transparency {
    OPAQUE {
        @Override
        public boolean isUseAlpha() {
            return false;
        }

        @Override
        public boolean isUseBlend() {
            return false;
        }
    },
    TRANSLUCENT {
        @Override
        public boolean isUseAlpha() {
            return true;
        }

        @Override
        public boolean isUseBlend() {
            return true;
        }
    },
    TRANSPARENT {
        @Override
        public boolean isUseAlpha() {
            return true;
        }

        @Override
        public boolean isUseBlend() {
            return false;
        }
    };

    public abstract boolean isUseAlpha();

    public abstract boolean isUseBlend();
}
