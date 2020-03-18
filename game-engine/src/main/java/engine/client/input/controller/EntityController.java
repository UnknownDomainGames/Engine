package engine.client.input.controller;

import engine.client.player.ClientPlayer;
import engine.entity.Entity;
import engine.graphics.camera.Camera;

public interface EntityController {

    void setPlayer(ClientPlayer player, Entity entity);

    void updateCamera(Camera camera, float tpf);

    void onInputMove(MotionType motionType, boolean state);

    /**
     * Handle client cursor move
     * @param x x-pos of the cursor
     * @param y y-pos of the cursor
     * @param cursorLock true if the cursor is invisible, i.e. the cursor is locked into the focused window
     */
    void onCursorMove(double x, double y, boolean cursorLock);
}
