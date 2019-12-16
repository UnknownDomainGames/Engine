package nullengine.client.gui.input;

import static nullengine.client.gui.input.Modifiers.Modifier.*;

public class Modifiers {

    private static final Modifiers[] KEY_MODIFIERS = new Modifiers[0x0f];

    static {
        for (int i = 0; i < 0x0f; i++) {
            KEY_MODIFIERS[i] = new Modifiers(i);
        }
    }

    public static Modifiers of() {
        return KEY_MODIFIERS[0];
    }

    public static Modifiers of(Modifier... modifiers) {
        int mods = 0;
        for (var mod : modifiers) {
            mods |= mod.code;
        }
        return KEY_MODIFIERS[mods];
    }

    public static Modifiers of(int mods) {
        return KEY_MODIFIERS[mods & 0x0f];
    }

    public enum Modifier {
        SHIFT(0x01),
        CONTROL(0x02),
        ALT(0x04),
        META(0x08);

        private final int code;

        Modifier(int code) {
            this.code = code;
        }
    }

    private final int mods;

    private Modifiers(int mods) {
        this.mods = mods & 0x0f;
    }

    public boolean isShift() {
        return (mods & SHIFT.code) != 0;
    }

    public boolean isControl() {
        return (mods & CONTROL.code) != 0;
    }

    public boolean isAlt() {
        return (mods & ALT.code) != 0;
    }

    public boolean isMeta() {
        return (mods & META.code) != 0;
    }

    public boolean is(Modifier... modifiers) {
        int mods = 0;
        for (var mod : modifiers) {
            mods |= mod.code;
        }
        return this.mods == mods;
    }

    public int getInternalCode() {
        return mods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Modifiers that = (Modifiers) o;
        return mods == that.mods;
    }

    @Override
    public int hashCode() {
        return mods;
    }

    @Override
    public String toString() {
        return "KeyModifier{" +
                "shift=" + isShift() +
                ", control=" + isControl() +
                ", alt=" + isAlt() +
                ", meta=" + isMeta() +
                '}';
    }
}
