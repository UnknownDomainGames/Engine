package com.github.unknownstudio.unknowndomain.engineapi.client.keybinding;

import com.github.unknownstudio.unknowndomain.engineapi.mod.ModContainer;
import com.github.unknownstudio.unknowndomain.engineapi.mod.ModUnloadListener;
import com.github.unknownstudio.unknowndomain.engineapi.registry.Registry;

public class KeyManager implements Registry<KeyBinding>,ModUnloadListener{

	public void onPressed(int key, int mods) {
		
	}
	
	public void onRelease(int key, int mods) {
		
	}

	@Override
	public KeyBinding register(KeyBinding obj) {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public void onModUnload(ModContainer mod) {
		// TODO 自动生成的方法存根
		
	}
}
