package nullengine.client.gui.input;

import nullengine.client.gui.internal.GUIPlatform;

public class Clipboard {
    public static String getString() {
        return GUIPlatform.getInstance().getClipboardHelper().getString();
    }

    public static void setString(String value) {
        GUIPlatform.getInstance().getClipboardHelper().setString(value);
    }
}
