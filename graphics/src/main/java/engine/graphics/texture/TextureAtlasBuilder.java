package engine.graphics.texture;

import engine.graphics.image.BufferedImage;
import engine.graphics.image.ReadOnlyImage;
import engine.math.Math2;

public class TextureAtlasBuilder {

    private BufferedImage result;
    private int size;

    private Node root;

    public TextureAtlasBuilder() {
        this(512);
    }

    public TextureAtlasBuilder(int size) {
        this.size = size;
        this.result = new BufferedImage(size);
        this.root = new Node(size);
    }

    public BufferedImage getResult() {
        return result;
    }

    public int getSize() {
        return size;
    }

    public TexCoord add(ReadOnlyImage image) {
        // Each image occupies an area which size is power of two.
        var regionSize = Math2.ceilPowerOfTwo(Math.max(image.getWidth(), image.getHeight()));
        var node = root.requestNode(image, regionSize);
        if (node == null) {
            resize(regionSize);
            node = root.requestNode(image, regionSize);
        }
        result.setImage(node.getX(), node.getY(), image);
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
        result = BufferedImage.resize(result, size);
    }

    public void finish() {
        root.resizeUv(0, 0, 1, 1);
    }

    public void dispose() {
        result = null;
    }

    public static final class TexCoord {
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

        private TexCoord uv;

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

        public TexCoord getUv() {
            if (uv == null) {
                uv = new TexCoord();
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
            float midU = (maxU - minU) / 2 + minU;
            float midV = (maxV - minV) / 2 + minV;
            resizeChildUv(children[TOP_LEFT], minU, minV, midU, midV);
            resizeChildUv(children[TOP_RIGHT], midU, minV, maxU, midV);
            resizeChildUv(children[BOTTOM_LEFT], minU, midV, midU, maxV);
            resizeChildUv(children[BOTTOM_RIGHT], midU, midV, maxU, maxV);
        }

        private void resizeChildUv(Node child, float minU, float minV, float maxU, float maxV) {
            if (child == null) return;
            child.resizeUv(minU, minV, maxU, maxV);
        }

        private Node requestNode(ReadOnlyImage image, int requestSize) {
            if (this.size == requestSize && !hasChildren()) {
                return this;
            }

            if (this.size < requestSize) return null;

            if (children == null) children = new Node[4];

            for (int i = 0; i < children.length; i++) {
                var child = getChild(i);
                if (child.isUsed()) continue;

                var node = child.requestNode(image, requestSize);
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
