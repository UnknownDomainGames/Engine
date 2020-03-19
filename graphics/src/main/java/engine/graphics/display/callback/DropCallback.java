package engine.graphics.display.callback;

import engine.graphics.display.Window;

import java.nio.file.Path;
import java.util.List;

@FunctionalInterface
public interface DropCallback {
    void invoke(Window window, List<Path> paths);
}
