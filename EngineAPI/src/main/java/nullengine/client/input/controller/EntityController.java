package nullengine.client.input.controller;

import nullengine.client.player.ClientPlayer;
import nullengine.client.rendering.camera.Camera;
import nullengine.entity.Entity;

public interface EntityController {

    void setPlayer(ClientPlayer player, Entity entity);

    void updateCamera(Camera camera, float tpf);

    void onInputMove(MotionType motionType, boolean state);

    void onCursorMove(double x, double y);
}
