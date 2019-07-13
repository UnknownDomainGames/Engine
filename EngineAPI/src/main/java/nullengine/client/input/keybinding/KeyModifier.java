package nullengine.client.input.keybinding;

import static nullengine.client.input.keybinding.KeyModifier.Modifier.*;

public class KeyModifier {

    private static final KeyModifier[] KEY_MODIFIERS = new KeyModifier[8];

    static {
        for (int i = 0; i < 8; i++) {
            KEY_MODIFIERS[i] = new KeyModifier(i);
        }
    }

    public static KeyModifier of() {
        return KEY_MODIFIERS[0];
    }

    public static KeyModifier of(Modifier... modifiers) {
        int mods = 0;
        for (var mod : modifiers) {
            mods |= mod.internalCode;
        }
        return KEY_MODIFIERS[mods];
    }

    public static KeyModifier of(int mods) {
        return KEY_MODIFIERS[mods & 0x07];
    }

    public enum Modifier {
        SHIFT(0x01),
        CONTROL(0x02),
        ALT(0x04);

        private final int internalCode;

        Modifier(int internalCode) {
            this.internalCode = internalCode;
        }
    }

    private final int mods;

    private KeyModifier(int mods) {
        this.mods = mods & 0x07;
    }

    public boolean isShift() {
        return (mods & SHIFT.internalCode) != 0;
    }

    public boolean isControl() {
        return (mods & CONTROL.internalCode) != 0;
    }

    public boolean isAlt() {
        return (mods & ALT.internalCode) != 0;
    }

    public boolean is(Modifier... modifiers) {
        int mods = 0;
        for (var mod : modifiers) {
            mods |= mod.internalCode;
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
        KeyModifier that = (KeyModifier) o;
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
                '}';
    }
}
