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
import unknowndomain.engine.api.util.DomainedPath;
import unknowndomain.engine.client.keybinding.ClientKeyBindingManager;
import unknowndomain.engine.client.keybinding.KeyBinding;

public class CameraController {
	public static final Vector3f UP_VECTOR = new Vector3f(0, 1, 0);
	private Camera camera;
	public CameraController(ClientKeyBindingManager clientKeyBindingManager,Camera camera) {
		float moveC = 0.05f;
        Vector3f tmp = new Vector3f();
		this.camera=camera;
		Consumer<Void> emptyConsumer=new Consumer<Void>(){
			@Override
			public void accept(Void t) {}
		};
		
		KeyBinding keyW=KeyBinding.create(KeyCode.KEY_W,ActionMode.PRESS,new Consumer<Void>(){//TODO: BUG
			@Override
			public void accept(Void t) {
				camera.move(0, 0, -1);
			}
			
		},emptyConsumer, KeyModifier.EMPTY);
		keyW.setRegistryName(new DomainedPath("","key_w"));
		
		KeyBinding keyA=KeyBinding.create(KeyCode.KEY_A,ActionMode.PRESS,new Consumer<Void>(){
			@Override
			public void accept(Void t) {
				camera.getFrontVector().mul(moveC, tmp);
				camera.getPosition().sub(tmp);
			}
			
		},emptyConsumer, KeyModifier.EMPTY);
		keyA.setRegistryName(new DomainedPath("","key_a"));
		
		KeyBinding keyS=KeyBinding.create(KeyCode.KEY_S,ActionMode.PRESS,new Consumer<Void>(){
			@Override
			public void accept(Void t) {
				camera.getFrontVector().cross(UP_VECTOR, tmp);
                tmp.mul(moveC);
                camera.getPosition().add(tmp);
			}
			
		},emptyConsumer, KeyModifier.EMPTY);
		keyS.setRegistryName(new DomainedPath("","key_s"));
		//TODO: undone
		clientKeyBindingManager.register(keyW);
		clientKeyBindingManager.register(keyA);
		clientKeyBindingManager.register(keyS);
		//clientKeyBindingManager.register(keyS);
		//clientKeyBindingManager.register(keyD);
	}
	

}
