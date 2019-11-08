package nullengine.client.rendering.display;

import java.util.List;
import java.util.function.Supplier;

public interface DisplayInfo {

    Monitor getPrimaryMonitor();

    List<Monitor> getMonitors();

    static DisplayInfo instance() {
        return DisplayInfo.Internal.instance.get();
    }

    class Internal {
        private static Supplier<DisplayInfo> instance = () -> {
            throw new IllegalStateException("DisplayInfo is not initialized.");
        };

        public static void setInstance(DisplayInfo instance) {
            DisplayInfo.Internal.instance = () -> instance;
        }
    }
}
