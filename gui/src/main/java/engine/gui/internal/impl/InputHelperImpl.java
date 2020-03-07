package engine.gui.internal.impl;

import engine.gui.internal.InputHelper;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.Validate;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.windows.User32;

public final class InputHelperImpl extends InputHelper {
    private long doubleClickTime = 500;

    public static void initialize() {
        InputHelperImpl inputHelper = new InputHelperImpl();
        setInstance(inputHelper);
        if (SystemUtils.IS_OS_WINDOWS) {
            long getDoubleClickTime = User32.getLibrary().getFunctionAddress("GetDoubleClickTime");
            if (getDoubleClickTime != MemoryUtil.NULL) setDoubleClickTime(JNI.invokeI(getDoubleClickTime));
        }
    }

    @Override
    public long _getDoubleClickTime() {
        return doubleClickTime;
    }

    @Override
    public void _setDoubleClickTime(long time) {
        Validate.inclusiveBetween(0, 5000, time);
        doubleClickTime = time == 0 ? 500 : time;
    }
}
