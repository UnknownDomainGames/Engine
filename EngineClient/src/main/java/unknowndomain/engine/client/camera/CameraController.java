package unknowndomain.engine.client.camera;

import java.util.function.Consumer;

import org.joml.Vector3f;
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
		this.setCamera(camera);
		Consumer<Void> emptyConsumer=new Consumer<Void>(){
			@Override
			public void accept(Void t) {}
		};
		boolean b=false;
		KeyBinding keyW=new KeyBinding(KeyCode.KEY_W) {
			@Override
			public void onActive() {
				camera.move(0, 0, -1);
			}
			@Override
			public void onInactive() {
				System.out.println("UN W");
			}
		};
		keyW.setRegistryName(new DomainedPath("","key_w"));
		
		KeyBinding keyA=KeyBinding.create(KeyCode.KEY_A,ActionMode.PRESS,new Consumer<Void>(){
			@Override
			public void accept(Void t) {
				camera.move(-1, 0, 0);
			}
		},emptyConsumer, KeyModifier.EMPTY);
		keyA.setRegistryName(new DomainedPath("","key_a"));
		
		KeyBinding keyS=KeyBinding.create(KeyCode.KEY_S,ActionMode.PRESS,new Consumer<Void>(){
			@Override
			public void accept(Void t) {
                camera.move(0, 0, 1);
			}
		},emptyConsumer, KeyModifier.EMPTY);
		keyS.setRegistryName(new DomainedPath("","key_s"));
		
		KeyBinding keyD=KeyBinding.create(KeyCode.KEY_D,ActionMode.PRESS,new Consumer<Void>(){
			@Override
			public void accept(Void t) {
				camera.move(1, 0, 0);
			}
		},emptyConsumer, KeyModifier.EMPTY);
		keyS.setRegistryName(new DomainedPath("","key_d"));
		
		clientKeyBindingManager.register(keyW);
		clientKeyBindingManager.register(keyA);
		clientKeyBindingManager.register(keyS);
		clientKeyBindingManager.register(keyD);
	}
	public Camera getCamera() {
		return camera;
	}
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
}
