package engine.graphics.display;

import java.util.List;

public interface Screen {
    long getPointer();

    String getName();

    int getPhysicsWidth();

    int getPhysicsHeight();

    float getScaleX();

    float getScaleY();

    int getPosX();

    int getPosY();

    VideoMode getVideoMode();

    List<VideoMode> getVideoModes();
}
