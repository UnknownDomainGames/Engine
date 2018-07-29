package unknowndomain.engine.client.camera;

import java.util.function.Consumer;

import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.glfw.GLFW;

import unknowndomain.engine.api.client.display.Camera;
import unknowndomain.engine.api.keybinding.ActionMode;
import unknowndomain.engine.api.keybinding.KeyCode;
import unknowndomain.engine.api.keybinding.KeyModifier;
import unknowndomain.engine.client.display.CameraDefault;
import unknowndomain.engine.client.keybinding.KeyBinding;

public class CameraController {
	private Camera camera;
	public CameraController(Camera camera) {
		this.camera=camera;
		KeyBinding.create(KeyCode.KEY_W,ActionMode.PRESS,new Consumer<Void>(){

			@Override
			public void accept(Void t) {
				System.out.println("HELLO");
			}
			
		}, new Consumer<Void>(){

			@Override
			public void accept(Void t) {
				System.out.println("BYE");
			}
			
		}, KeyModifier.EMPTY);
	}
	

}
