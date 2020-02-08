package engine.gui.misc;

public class IndexRange {
    private int start;
    private int end;

    public IndexRange(int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException(String.format("start(%d) > end(%d)", start, end));
        }

        this.start = start;
        this.end = end;
    }

    public IndexRange(IndexRange range) {
        start = range.start;
        end = range.end;
    }

    public int getStart() {
        return start;
    }

    /**
     * Return the end position of the range (exclusive)
     *
     * @return
     */
    public int getEnd() {
        return end;
    }

    public int length() {
        return end - start;
    }

    public boolean isInRange(int index) {
        return start <= index && index < end;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof IndexRange) {
            IndexRange range = ((IndexRange) obj);
            return range.start == start && range.end == end;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * start + end;
    }

    @Override
    public String toString() {
        return String.format("[%d..%d)", start, end);
    }

    public static IndexRange ofOrderless(int i, int j) {
        return new IndexRange(Math.min(i, j), Math.max(i, j));
    }
}
