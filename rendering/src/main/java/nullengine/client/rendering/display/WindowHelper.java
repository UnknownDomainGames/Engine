package nullengine.client.rendering.display;

import java.util.Collection;

public interface WindowHelper {

    Collection<Monitor> getMonitors();

    Monitor getPrimaryMonitor();

    Window createWindow();
}
