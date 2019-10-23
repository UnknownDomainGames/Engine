package nullengine.client.rendering.display;

import java.util.List;

public interface DisplayInfo {

    Monitor getPrimaryMonitor();

    List<Monitor> getMonitors();
}
