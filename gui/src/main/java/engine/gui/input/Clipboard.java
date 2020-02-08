package engine.gui.input;

import engine.gui.internal.GUIPlatform;

public class Clipboard {
    public static String getString() {
        return GUIPlatform.getInstance().getClipboardHelper().getString();
    }

    public static void setString(String value) {
        GUIPlatform.getInstance().getClipboardHelper().setString(value);
    }
}
