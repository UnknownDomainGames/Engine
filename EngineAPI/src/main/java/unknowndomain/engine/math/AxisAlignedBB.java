package unknowndomain.engine.math;

public interface AxisAlignedBB {

    float getX();

    float getY();

    float getZ();

    float getWidth();

    float getHeight();

    float getDepth();

    boolean conflict(AxisAlignedBB other);
}
