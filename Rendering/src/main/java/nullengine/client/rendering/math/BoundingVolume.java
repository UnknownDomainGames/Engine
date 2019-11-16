package nullengine.client.rendering.math;

import org.joml.AABBf;
import org.joml.FrustumIntersection;
import org.joml.Spheref;

public class BoundingVolume {

    private AABBf box;
    private Spheref sphere;

    private Type boundingType;

    private boolean dirty;

    public enum Type {
        Box, Sphere
    }

    public BoundingVolume() {
    }

    public BoundingVolume(AABBf box) {
        setBox(box);
    }

    public BoundingVolume(Spheref sphere) {
        setSphere(sphere);
    }

    public AABBf getBox() {
        if (dirty && boundingType == Type.Sphere) {
            final float x = sphere.x, y = sphere.y, z = sphere.z, r = sphere.r;
            box = new AABBf(x - r, y - r, z - r, x + r, y + r, z + r);
        }
        return box;
    }

    public void setBox(AABBf box) {
        this.boundingType = Type.Box;
        this.box = box;
        this.dirty = true;
    }

    public Spheref getSphere() {
        if (dirty && boundingType == Type.Box) {
            final float dx = box.maxX - box.minX,
                    dy = box.maxY - box.minY,
                    dz = box.maxZ - box.minZ;
            final float d = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            sphere = new Spheref(box.minX + dx / 2, box.minY + dy / 2, box.maxZ + dz / 2, d / 2);
        }
        return sphere;
    }

    public void setSphere(Spheref sphere) {
        this.boundingType = Type.Sphere;
        this.sphere = sphere;
        this.dirty = true;
    }

    public Type getType() {
        return boundingType;
    }

    public boolean test(FrustumIntersection intersection) {
        if (boundingType == Type.Box) {
            return intersection.testAab(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
        } else if (boundingType == Type.Sphere) {
            return intersection.testSphere(sphere.x, sphere.y, sphere.z, sphere.r);
        } else {
            return false;
        }
    }
}