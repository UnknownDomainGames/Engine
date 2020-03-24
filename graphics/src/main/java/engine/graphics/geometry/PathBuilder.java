package engine.graphics.geometry;

import java.nio.ByteBuffer;

public class PathBuilder {
    private ByteBuffer buffer;

    private float startX;
    private float startY;
    private float prevX;
    private float prevY;

    public PathBuilder heap() {
        buffer = buffer == null ? ByteBuffer.allocate(256) : ByteBuffer.allocate(buffer.capacity()).put(buffer);
        return this;
    }

    public PathBuilder direct() {
        buffer = buffer == null ? ByteBuffer.allocateDirect(256) : ByteBuffer.allocateDirect(buffer.capacity()).put(buffer);
        return this;
    }

    public PathBuilder ensureCapacity(int capacity) {
        if (buffer == null) throw new IllegalStateException("No initialize buffer");
        int oldCapacity = buffer.capacity();
        if (oldCapacity >= capacity) return this;
        int newCapacity = Math.max(capacity, oldCapacity << 1 + oldCapacity);
        if (buffer.isDirect()) {
            buffer = ByteBuffer.allocateDirect(newCapacity).put(buffer);
        } else {
            buffer = ByteBuffer.allocate(newCapacity).put(buffer);
        }
        return this;
    }

    public PathBuilder moveTo(float x, float y) {
        startX = prevX = x;
        startY = prevY = y;
        append(x, y);
        return this;
    }

    public PathBuilder lineTo(float x, float y) {
        prevX = x;
        prevY = y;
        append(x, y);
        return this;
    }

    public PathBuilder quadTo(float x, float y, float px, float py) {
        //TODO: optimization
        float step = (Math.abs(prevX - px) + Math.abs(px - x) + Math.abs(prevY - py) + Math.abs(py - y)) / 12f;
        for (float f = step; f < 1f; f += step) {
            float f2 = 1 - f;
            append(prevX * f2 * f2 + px * f2 * f * 2 + x * f * f, prevY * f2 * f2 + py * f2 * f * 2 + y * f * f);
        }
        prevX = x;
        prevY = y;
        append(x, y);
        return this;
    }

    public PathBuilder curveTo(float x, float y, float px0, float py0, float px1, float py1) {
        //TODO: optimization
        float step = (Math.abs(prevX - px0) + Math.abs(px0 - px1) + Math.abs(px1 - x) +
                Math.abs(prevY - py0) + Math.abs(py0 - py1) + Math.abs(py1 - y)) / 36f;
        for (float f = step; f < 1f; f += step) {
            float f2 = 1 - f;
            append(prevX * f2 * f2 * f2 + px0 * f2 * f2 * f * 3 + px1 * f2 * f * f * 3 + x * f * f * f,
                    prevY * f2 * f2 * f2 + py0 * f2 * f2 * f * 3 + py1 * f2 * f * f * 3 + y * f * f * f);
        }
        prevX = x;
        prevY = y;
        append(x, y);
        return this;
    }

    // https://stackoverflow.com/questions/43946153/approximating-svg-elliptical-arc-in-canvas-with-javascript
    public PathBuilder arcTo(float x, float y, float radiusX, float radiusY, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag) {
        float phi = (float) Math.toRadians(xAxisRotation);
        float rX = Math.abs(radiusX);
        float rY = Math.abs(radiusY);

        float dx2 = (prevX - x) / 2;
        float dy2 = (prevY - y) / 2;

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

        float cx = (float) (Math.cos(phi) * cxp - Math.sin(phi) * cyp + (prevX + x) / 2);
        float cy = (float) (Math.sin(phi) * cxp + Math.cos(phi) * cyp + (prevY + y) / 2);

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

    public PathBuilder close() {
        prevX = startX;
        prevY = startY;
        append(startX, startY);
        return this;
    }

    private void append(float x, float y) {
        ensureCapacity(buffer.capacity() + 8);
        buffer.putFloat(x).putFloat(y);
    }

    private ByteBuffer build() {
        return buffer.flip();
    }
}
