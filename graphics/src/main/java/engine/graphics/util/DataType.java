package engine.graphics.util;

public enum DataType {

    BYTE(Byte.BYTES),
    UNSIGNED_BYTE(Byte.BYTES),
    SHORT(Short.BYTES),
    UNSIGNED_SHORT(Short.BYTES),
    INT(Integer.BYTES),
    UNSIGNED_INT(Integer.BYTES),
    FLOAT(Float.BYTES),
    DOUBLE(Double.BYTES),
    HALF_FLOAT(2);

    private final int bytes;

    DataType(int bytes) {
        this.bytes = bytes;
    }

    public int getBytes() {
        return bytes;
    }
}
