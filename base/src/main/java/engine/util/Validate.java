package engine.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class Validate {
    private static final String DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE =
            "The value %s is not in the specified inclusive range of %s to %s";
    private static final String DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE =
            "The value %s is not in the specified exclusive range of %s to %s";

    public static <T> T notNull(T object) {
        if (object == null) {
            throw new NullPointerException();
        }
        return object;
    }

    public static <T> T notNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    public static <T> T notNull(T object, String message, Object... args) {
        if (object == null) {
            throw new NullPointerException(String.format(message, args));
        }
        return object;
    }

    public static <T extends CharSequence> T notEmpty(T cs) {
        if (cs == null) {
            throw new NullPointerException();
        }
        if (cs.length() == 0) {
            throw new IllegalArgumentException();
        }
        return cs;
    }

    public static <T extends CharSequence> T notEmpty(T cs, String message) {
        if (cs == null) {
            throw new NullPointerException(message);
        }
        if (cs.length() == 0) {
            throw new IllegalArgumentException(message);
        }
        return cs;
    }

    public static <T extends CharSequence> T notEmpty(T cs, String message, Object... args) {
        if (cs == null) {
            throw new NullPointerException(String.format(message, args));
        }
        if (cs.length() == 0) {
            throw new IllegalArgumentException(String.format(message, args));
        }
        return cs;
    }

    public static <T extends Collection<?>> T notEmpty(T collection) {
        if (collection == null) {
            throw new NullPointerException();
        }
        if (collection.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return collection;
    }

    public static <T extends Collection<?>> T notEmpty(T collection, String message) {
        if (collection == null) {
            throw new NullPointerException(message);
        }
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return collection;
    }

    public static <T extends Collection<?>> T notEmpty(T collection, String message, Object... args) {
        if (collection == null) {
            throw new NullPointerException(String.format(message, args));
        }
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(String.format(message, args));
        }
        return collection;
    }

    public static <T extends Map<?, ?>> T notEmpty(T map) {
        if (map == null) {
            throw new NullPointerException();
        }
        if (map.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return map;
    }

    public static <T extends Map<?, ?>> T notEmpty(T map, String message) {
        if (map == null) {
            throw new NullPointerException(message);
        }
        if (map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return map;
    }

    public static <T extends Map<?, ?>> T notEmpty(T map, String message, Object... args) {
        if (map == null) {
            throw new NullPointerException(String.format(message, args));
        }
        if (map.isEmpty()) {
            throw new IllegalArgumentException(String.format(message, args));
        }
        return map;
    }

    public static <T> T[] notEmpty(T[] array) {
        if (array == null) {
            throw new NullPointerException();
        }
        if (array.length == 0) {
            throw new IllegalArgumentException();
        }
        return array;
    }

    public static <T> T[] notEmpty(T[] array, String message) {
        if (array == null) {
            throw new NullPointerException(message);
        }
        if (array.length == 0) {
            throw new IllegalArgumentException(message);
        }
        return array;
    }

    public static <T> T[] notEmpty(T[] array, String message, Object... args) {
        if (array == null) {
            throw new NullPointerException(String.format(message, args));
        }
        if (array.length == 0) {
            throw new IllegalArgumentException(String.format(message, args));
        }
        return array;
    }

    public static <T extends CharSequence> T notBlank(T cs) {
        if (cs == null) {
            throw new NullPointerException();
        }
        if (StringUtils.isBlank(cs)) {
            throw new IllegalArgumentException();
        }
        return cs;
    }

    public static <T extends CharSequence> T notBlank(T cs, String message) {
        if (cs == null) {
            throw new NullPointerException(message);
        }
        if (StringUtils.isBlank(cs)) {
            throw new IllegalArgumentException(message);
        }
        return cs;
    }

    public static <T extends CharSequence> T notBlank(T cs, String message, Object... args) {
        if (cs == null) {
            throw new NullPointerException(String.format(message, args));
        }
        if (StringUtils.isBlank(cs)) {
            throw new IllegalArgumentException(String.format(message, args));
        }
        return cs;
    }

    public static <T extends Iterable<?>> T noNullElements(T iterable) {
        if (iterable == null) {
            throw new NullPointerException();
        }
        for (Iterator<?> it = iterable.iterator(); it.hasNext(); ) {
            if (it.next() == null) {
                throw new IllegalArgumentException();
            }
        }
        return iterable;
    }

    public static <T extends Iterable<?>> T noNullElements(T iterable, String message) {
        if (iterable == null) {
            throw new NullPointerException(message);
        }
        for (Iterator<?> it = iterable.iterator(); it.hasNext(); ) {
            if (it.next() == null) {
                throw new IllegalArgumentException(message);
            }
        }
        return iterable;
    }

    public static <T extends Iterable<?>> T noNullElements(T iterable, String message, Object... args) {
        if (iterable == null) {
            throw new NullPointerException(String.format(message, args));
        }
        for (Iterator<?> it = iterable.iterator(); it.hasNext(); ) {
            if (it.next() == null) {
                throw new IllegalArgumentException(String.format(message, args));
            }
        }
        return iterable;
    }

    public static <T> T[] noNullElements(T[] array) {
        if (array == null) {
            throw new NullPointerException();
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                throw new IllegalArgumentException();
            }
        }
        return array;
    }

    public static <T> T[] noNullElements(T[] array, String message) {
        if (array == null) {
            throw new NullPointerException(message);
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                throw new IllegalArgumentException(message);
            }
        }
        return array;
    }

    public static <T> T[] noNullElements(T[] array, String message, Object... args) {
        if (array == null) {
            throw new NullPointerException(String.format(message, args));
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                throw new IllegalArgumentException(String.format(message, args));
            }
        }
        return array;
    }

    public static double notNaN(double value) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static double notNaN(double value, String message) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static double notNaN(double value, String message, Object... args) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException(String.format(message, args));
        }
        return value;
    }

    public static float notNaN(float value) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static float notNaN(float value, String message) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static float notNaN(float value, String message, Object... args) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException(String.format(message, args));
        }
        return value;
    }

    public static double finite(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static double finite(double value, String message) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static double finite(double value, String message, Object... args) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(String.format(message, args));
        }
        return value;
    }

    public static float finite(float value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static float finite(float value, String message) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static float finite(float value, String message, Object... args) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(String.format(message, args));
        }
        return value;
    }

    public static <T> void inclusiveBetween(final T start, final T end, final Comparable<T> value) {
        if (value.compareTo(start) < 0 || value.compareTo(end) > 0) {
            throw new IllegalArgumentException(String.format(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static <T> void inclusiveBetween(final T start, final T end, final Comparable<T> value, final String message) {
        if (value.compareTo(start) < 0 || value.compareTo(end) > 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void inclusiveBetween(final long start, final long end, final long value) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(String.format(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static void inclusiveBetween(final long start, final long end, final long value, final String message) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void inclusiveBetween(final double start, final double end, final double value) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(String.format(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static void inclusiveBetween(final double start, final double end, final double value, final String message) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> void exclusiveBetween(final T start, final T end, final Comparable<T> value) {
        if (value.compareTo(start) <= 0 || value.compareTo(end) >= 0) {
            throw new IllegalArgumentException(String.format(DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static <T> void exclusiveBetween(final T start, final T end, final Comparable<T> value, final String message) {
        if (value.compareTo(start) <= 0 || value.compareTo(end) >= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void exclusiveBetween(final long start, final long end, final long value) {
        if (value <= start || value >= end) {
            throw new IllegalArgumentException(String.format(DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static void exclusiveBetween(final long start, final long end, final long value, final String message) {
        if (value <= start || value >= end) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void exclusiveBetween(final double start, final double end, final double value) {
        if (value <= start || value >= end) {
            throw new IllegalArgumentException(String.format(DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static void exclusiveBetween(final double start, final double end, final double value, final String message) {
        if (value <= start || value >= end) {
            throw new IllegalArgumentException(message);
        }
    }
}
