package unknowndomain.engine.util;

import org.apache.commons.lang3.Validate;
import unknowndomain.engine.math.Math2;

public class BitArray {

    private final long[] array;

    private final int bitsPreEntry;
    private final long maxEntryValue;
    private final int length;

    public BitArray(int bitsPreEntry, int length) {
        Validate.inclusiveBetween(1L, 31L, bitsPreEntry); // TODO: Support 32bits.
        this.bitsPreEntry = bitsPreEntry;
        this.maxEntryValue = (1L << bitsPreEntry) - 1;
        this.length = length;
        this.array = new long[Math2.ceil(bitsPreEntry * length, 64) >> 6];
    }

    public int getBitsPreEntry() {
        return bitsPreEntry;
    }

    public int length() {
        return length;
    }

    public long getMaxEntryValue() {
        return maxEntryValue;
    }

    public int get(int index) {
        int bitStartIndex = index * bitsPreEntry;
        int arrayStartIndex = bitStartIndex >>> 6; // bitStartIndex / 64
        int arrayEndIndex = (bitStartIndex + bitsPreEntry - 1) >>> 6;
        Validate.inclusiveBetween(0, array.length - 1, arrayEndIndex);
        int offset = bitStartIndex & 63; // bitStartIndex % 64

        if (arrayStartIndex == arrayEndIndex) {
            return (int) (array[arrayStartIndex] >>> offset & maxEntryValue);
        } else {
            return (int) ((array[arrayStartIndex] >>> offset | array[arrayEndIndex] << (64 - offset)) & this.maxEntryValue);
        }
    }

    public void set(int index, int value) {
        Validate.inclusiveBetween(0, maxEntryValue, value);
        int bitStartIndex = index * bitsPreEntry;
        int arrayStartIndex = bitStartIndex >>> 6; // bitStartIndex / 64
        int arrayEndIndex = (bitStartIndex + bitsPreEntry - 1) >>> 6;
        Validate.inclusiveBetween(0, array.length - 1, arrayEndIndex);
        int offset = bitStartIndex & 63; // bitStartIndex % 64

        array[arrayStartIndex] = array[arrayStartIndex] & ~(maxEntryValue << offset) | (long) value << offset;

        if (arrayStartIndex != arrayEndIndex) {
            offset = 64 - offset;
            int offsetEnd = bitsPreEntry - offset;
            this.array[arrayEndIndex] = this.array[arrayEndIndex] >>> offsetEnd << offsetEnd | (long) value >> offset;
        }
    }

    public int getAndSet(int index, int value) {
        Validate.inclusiveBetween(0, maxEntryValue, value);
        int bitStartIndex = index * bitsPreEntry;
        int arrayStartIndex = bitStartIndex >>> 6; // bitStartIndex / 64
        int arrayEndIndex = (bitStartIndex + bitsPreEntry - 1) >>> 6;
        Validate.inclusiveBetween(0, array.length - 1, arrayEndIndex);
        int offset = bitStartIndex & 63; // bitStartIndex % 64

        long oldValue = array[arrayStartIndex] >>> offset;
        array[arrayStartIndex] = array[arrayStartIndex] & ~(maxEntryValue << offset) | (long) value << offset;
        if (arrayStartIndex != arrayEndIndex) {
            offset = 64 - offset;
            oldValue |= array[arrayEndIndex] << offset;
            int offsetEnd = bitsPreEntry - offset;
            this.array[arrayEndIndex] = this.array[arrayEndIndex] >>> offsetEnd << offsetEnd | (long) value >> offset;
        }

        return (int) (oldValue & maxEntryValue);
    }

    public long[] getBackingArray() {
        return array;
    }
}
