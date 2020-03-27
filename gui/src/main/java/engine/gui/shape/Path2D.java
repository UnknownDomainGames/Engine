package engine.gui.shape;

import engine.graphics.vertex.VertexDataBuf;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Path2D {

    private static final float tessTol = 1f;

    private FloatBuffer buffer;

    private float moveX;
    private float moveY;
    private float currX;
    private float currY;

    public FloatBuffer getBuffer() {
        return buffer;
    }

    public float[] get(float[] dst) {
        return get(0, dst);
    }

    public float[] get(int index, float[] dst) {
        buffer.get(dst, index, buffer.position());
        return dst;
    }

    public VertexDataBuf get(VertexDataBuf buf) {
        for (int i = 0; i < buffer.position(); i += 2) {
            buf.pos(buffer.get(i), buffer.get(i + 1), 0);
        }
        return buf;
    }

    public Path2D heap() {
        buffer = buffer == null ? FloatBuffer.allocate(64) :
                FloatBuffer.allocate(buffer.capacity()).put(buffer);
        return this;
    }

    public boolean isDirect() {
        return buffer != null && buffer.isDirect();
    }

    public Path2D direct() {
        buffer = buffer == null ? ByteBuffer.allocateDirect(64 * Float.BYTES).asFloatBuffer() :
                ByteBuffer.allocateDirect(buffer.capacity() * Float.BYTES).asFloatBuffer().put(buffer);
        return this;
    }

    public Path2D ensureCapacity(int capacity) {
        if (buffer == null) throw new IllegalStateException("No initialize buffer");
        int oldCapacity = buffer.capacity();
        if (oldCapacity >= capacity) return this;
        int newCapacity = Math.max(capacity, oldCapacity << 1 + oldCapacity);
        if (buffer.isDirect()) {
            buffer = ByteBuffer.allocateDirect(newCapacity).asFloatBuffer().put(buffer);
        } else {
            buffer = FloatBuffer.allocate(newCapacity).put(buffer);
        }
        return this;
    }

    public float getCurrentX() {
        return currX;
    }

    public float getCurrentY() {
        return currY;
    }

    public Path2D moveTo(float x, float y) {
        moveX = currX = x;
        moveY = currY = y;
        append(x, y);
        return this;
    }

    public Path2D lineTo(float x, float y) {
        currX = x;
        currY = y;
        append(x, y);
        return this;
    }

    /**
     * Draw a quadratic Bezier curve
     */
    public Path2D quadTo(float px, float py, float x, float y) {
        float px0 = currX + 2.0f / 3.0f * (px - currX);
        float py0 = currY + 2.0f / 3.0f * (py - currY);
        float px1 = x + 2.0f / 3.0f * (px - x);
        float py1 = y + 2.0f / 3.0f * (py - y);
        return curveTo(x, y, px0, py0, px1, py1);
    }

    /**
     * Draw a Bezier curve
     */
    public Path2D curveTo(float px0, float py0, float px1, float py1, float x, float y) {
        tesselateBezier(currX, currY, px0, py0, px1, py1, x, y, 0);
        currX = x;
        currY = y;
        append(x, y);
        return this;
    }

    /**
     * Tesselate bezier curve.
     * <br/>
     * Copy from <a href="https://github.com/memononen/nanovg">nanovg</a> and modified by Mouse0w0. These code licensed
     * under zLib license.
     */
    private void tesselateBezier(float x1, float y1, float x2, float y2,
                                 float x3, float y3, float x4, float y4,
                                 int level) {
        float x12, y12, x23, y23, x34, y34, x123, y123, x234, y234, x1234, y1234;
        float dx, dy, d2, d3;

        if (level > 10) return;

        x12 = (x1 + x2) * 0.5f;
        y12 = (y1 + y2) * 0.5f;
        x23 = (x2 + x3) * 0.5f;
        y23 = (y2 + y3) * 0.5f;
        x34 = (x3 + x4) * 0.5f;
        y34 = (y3 + y4) * 0.5f;
        x123 = (x12 + x23) * 0.5f;
        y123 = (y12 + y23) * 0.5f;

        dx = x4 - x1;
        dy = y4 - y1;
        d2 = Math.abs(((x2 - x4) * dy - (y2 - y4) * dx));
        d3 = Math.abs(((x3 - x4) * dy - (y3 - y4) * dx));

        if ((d2 + d3) * (d2 + d3) < tessTol * (dx * dx + dy * dy)) {
            append(x4, y4);
            return;
        }

        x234 = (x23 + x34) * 0.5f;
        y234 = (y23 + y34) * 0.5f;
        x1234 = (x123 + x234) * 0.5f;
        y1234 = (y123 + y234) * 0.5f;

        tesselateBezier(x1, y1, x12, y12, x123, y123, x1234, y1234, level + 1);
        tesselateBezier(x1234, y1234, x234, y234, x34, y34, x4, y4, level + 1);
    }

    /**
     * Draw a elliptical arc
     */
    // https://stackoverflow.com/questions/43946153/approximating-svg-elliptical-arc-in-canvas-with-javascript
    public Path2D arcTo(float radiusX, float radiusY, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag, float x, float y) {
        float phi = (float) Math.toRadians(xAxisRotation);
        float rX = Math.abs(radiusX);
        float rY = Math.abs(radiusY);

        float dx2 = (currX - x) / 2;
        float dy2 = (currY - y) / 2;

        float x1p = (float) (Math.cos(phi) * dx2 + Math.sin(phi) * dy2);
        float y1p = (float) (-Math.sin(phi) * dx2 + Math.cos(phi) * dy2);

        float rxs = rX * rX;
        float rys = rY * rY;
        float x1ps = x1p * x1p;
        float y1ps = y1p * y1p;

        float cr = x1ps / rxs + y1ps / rys;
        if (cr > 1) {
            float s = (float) Math.sqrt(cr);
            rX = s * rX;
            rY = s * rY;
            rxs = rX * rX;
            rys = rY * rY;
        }

        float dq = (rxs * y1ps + rys * x1ps);
        float pq = (rxs * rys - dq) / dq;
        float q = (float) Math.sqrt(Math.max(0, pq));
        if (largeArcFlag == sweepFlag)
            q = -q;
        float cxp = q * rX * y1p / rY;
        float cyp = -q * rY * x1p / rX;

        float cx = (float) (Math.cos(phi) * cxp - Math.sin(phi) * cyp + (currX + x) / 2);
        float cy = (float) (Math.sin(phi) * cxp + Math.cos(phi) * cyp + (currY + y) / 2);

        float theta = angle(1, 0, (x1p - cxp) / rX, (y1p - cyp) / rY);

        float delta = angle(
                (x1p - cxp) / rX, (y1p - cyp) / rY,
                (-x1p - cxp) / rX, (-y1p - cyp) / rY);

        delta = (float) (delta - Math.PI * 2 * Math.floor(delta / (Math.PI * 2)));

        if (!sweepFlag)
            delta -= 2 * Math.PI;

        float n1 = theta,
                n2 = delta;

        // E(n)
        // cx +acosθcosη−bsinθsinη
        // cy +asinθcosη+bcosθsinη

        float[] en1 = E(n1, cx, cy, radiusX, radiusY, phi);
        float[] en2 = E(n2, cx, cy, radiusX, radiusY, phi);
        float[] edn1 = Ed(n1, radiusX, radiusY, phi);
        float[] edn2 = Ed(n2, radiusX, radiusY, phi);

        float alpha = (float) (Math.sin(n2 - n1) * (Math.sqrt(4 + 3 * Math.pow(Math.tan((n2 - n1) / 2), 2)) - 1) / 3);

        curveTo(x, y, en1[0] + alpha * edn1[0], en1[1] + alpha * edn1[1], en2[0] - alpha * edn2[0], en2[1] - alpha * edn2[1]);
        return this;
    }

    private float[] E(float n, float cx, float cy, float rx, float ry, float phi) {
        float enx = (float) (cx + rx * Math.cos(phi) * Math.cos(n) - ry * Math.sin(phi) * Math.sin(n));
        float eny = (float) (cy + rx * Math.sin(phi) * Math.cos(n) + ry * Math.cos(phi) * Math.sin(n));
        return new float[]{enx, eny};
    }

    // E'(n)
    // −acosθsinη−bsinθcosη
    // −asinθsinη+bcosθcosη
    private float[] Ed(float n, float rx, float ry, float phi) {
        float ednx = (float) (-1 * rx * Math.cos(phi) * Math.sin(n) - ry * Math.sin(phi) * Math.cos(n));
        float edny = (float) (-1 * rx * Math.sin(phi) * Math.sin(n) + ry * Math.cos(phi) * Math.cos(n));
        return new float[]{ednx, edny};
    }

    private float angle(float ux, float uy, float vx, float vy) {
        float dot = ux * vx + uy * vy;
        double len = Math.sqrt(ux * ux + uy * uy) * Math.sqrt(vx * vx + vy * vy);

        double angle = Math.acos(Math.min(Math.max(dot / len, -1), 1));
        if ((ux * vy - uy * vx) < 0)
            angle = -angle;
        return (float) angle;
    }

    public Path2D closePath() {
        currX = moveX;
        currY = moveY;
        append(moveX, moveY);
        return this;
    }

    public void reset() {
        buffer.clear();
    }

    private void append(float x, float y) {
        ensureCapacity(buffer.position() + 2);
        buffer.put(x).put(y);
    }
}
