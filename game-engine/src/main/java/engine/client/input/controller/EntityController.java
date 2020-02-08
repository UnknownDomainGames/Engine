package engine.client.input.controller;

import engine.client.player.ClientPlayer;
import engine.entity.Entity;
import engine.graphics.camera.Camera;

public interface EntityController {

    void setPlayer(ClientPlayer player, Entity entity);

    void updateCamera(Camera camera, float tpf);

    void onInputMove(MotionType motionType, boolean state);

    void onCursorMove(double x, double y);
}
