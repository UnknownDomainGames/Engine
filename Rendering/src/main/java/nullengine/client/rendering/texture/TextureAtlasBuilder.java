package nullengine.client.rendering.texture;

import nullengine.client.rendering.math.Math2;

public class TextureAtlasBuilder {

    private Texture2DBuffer texture;
    private int size;

    private Node root;

    public TextureAtlasBuilder() {
        this(512);
    }

    public TextureAtlasBuilder(int size) {
        this.size = size;
        this.texture = new Texture2DBuffer(size);
        this.root = new Node(size);
    }

    public Texture2DBuffer getTexture() {
        return texture;
    }

    public int getSize() {
        return size;
    }

    public UVResult add(Texture2DBuffer texture) {
        // TODO: support texture size isn't power of two.
        var regionSize = Math2.ceilPowerOfTwo(Math.max(texture.getWidth(), texture.getHeight()));
        var node = root.requestNode(texture, regionSize);
        if (node == null) {
            resize(regionSize);
            node = root.requestNode(texture, regionSize);
        }
        this.texture.setTexture(node.getX(), node.getY(), texture);
        return node.getUv();
    }

    private void resize(int requiredSize) {
        int factor = (int) Math.ceil(1D * requiredSize / size);
        for (int i = 0; i < factor; i++) {
            size *= 2;
            var newRoot = new Node(size);
            newRoot.setChild(Node.TOP_LEFT, root);
            root = newRoot;
        }
        var newTexture = new Texture2DBuffer(size);
        newTexture.setTexture(0, 0, texture);
        texture = newTexture;
    }

    public void finish() {
        root.resizeUv(0, 0, 1, 1);
    }

    public void dispose() {
        texture = null;
    }

    public static final class UVResult {
        private float minU;
        private float minV;
        private float maxU;
        private float maxV;

        public float getMinU() {
            return minU;
        }

        public float getMinV() {
            return minV;
        }

        public float getMaxU() {
            return maxU;
        }

        public float getMaxV() {
            return maxV;
        }
    }

    public static class Node {
        private static final int TOP_LEFT = 0;
        private static final int TOP_RIGHT = 1;
        private static final int BOTTOM_LEFT = 2;
        private static final int BOTTOM_RIGHT = 3;

        private Node parent;
        private int x;
        private int y;
        private int size;

        private UVResult uv;

        private boolean used;
        private Node[] children;

        private Node(int size) {
            this(null, 0, 0, size);
        }

        private Node(Node parent, int x, int y, int size) {
            this.parent = parent;
            this.x = x;
            this.y = y;
            this.size = size;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getSize() {
            return size;
        }

        public UVResult getUv() {
            if (uv == null) {
                uv = new UVResult();
                used = true;
                if (parent != null) parent.notifyChildUsed();
            }
            return uv;
        }

        private boolean isUsed() {
            return used;
        }

        private boolean hasChildren() {
            return children != null;
        }

        private void notifyChildUsed() {
            for (var child : children)
                if (child == null || !child.isUsed()) return;
            used = true;
        }

        private void resizeUv(float minU, float minV, float maxU, float maxV) {
            if (uv != null) {
                uv.minU = minU;
                uv.minV = minV;
                uv.maxU = maxU;
                uv.maxV = maxV;
            }

            if (children == null) return;
            float midU = (maxU - minU) / 2;
            float midV = (maxV - minV) / 2;
            resizeChildUv(children[TOP_LEFT], minU, minV, midU, midV);
            resizeChildUv(children[TOP_RIGHT], midU, minV, maxU, midV);
            resizeChildUv(children[BOTTOM_LEFT], minU, midV, midU, maxV);
            resizeChildUv(children[BOTTOM_RIGHT], midU, midV, maxU, maxV);
        }

        private void resizeChildUv(Node child, float minU, float minV, float maxU, float maxV) {
            if (child == null) return;
            child.resizeUv(minU, minV, maxU, maxV);
        }

        private Node requestNode(Texture2DBuffer texture, int requestSize) {
            if (this.size == requestSize && !hasChildren()) {
                return this;
            }

            if (this.size < requestSize) return null;

            if (children == null) children = new Node[4];

            for (int i = 0; i < children.length; i++) {
                var child = getChild(i);
                if (child.isUsed()) continue;

                var node = child.requestNode(texture, requestSize);
                if (node != null) return node;
            }
            return null;
        }

        private Node getChild(int index) {
            var child = children[index];
            if (child != null) return child;

            var childSize = size / 2;
            if (index == TOP_LEFT)
                child = new Node(this, x, y, childSize);
            else if (index == TOP_RIGHT)
                child = new Node(this, x + childSize, y, childSize);
            else if (index == BOTTOM_LEFT)
                child = new Node(this, x, y + childSize, childSize);
            else if (index == BOTTOM_RIGHT)
                child = new Node(this, x + childSize, y + childSize, childSize);
            return children[index] = child;
        }

        private void setChild(int index, Node child) {
            if (children == null) children = new Node[4];
            children[index] = child;
        }
    }
}
