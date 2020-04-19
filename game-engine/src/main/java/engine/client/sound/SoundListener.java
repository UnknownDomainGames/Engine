package engine.client.sound;

import engine.graphics.camera.Camera;
import org.joml.Vector3fc;

public interface SoundListener {
    SoundListener camera(Camera camera);

    SoundListener speed(float x, float y, float z);

    SoundListener speed(Vector3fc speed);

    SoundListener position(float x, float y, float z);

    SoundListener position(Vector3fc pos);

    SoundListener orient(Vector3fc target, Vector3fc up);

    SoundListener orient(Vector3fc target);

    SoundListener orient(float x, float y, float z);
}
