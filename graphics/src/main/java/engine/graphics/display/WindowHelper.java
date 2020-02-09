package engine.graphics.display;

import java.util.Collection;

public interface WindowHelper {

    Collection<Screen> getScreens();

    Screen getPrimaryScreen();

    Screen getScreen(String name);

    Window createWindow();
}
