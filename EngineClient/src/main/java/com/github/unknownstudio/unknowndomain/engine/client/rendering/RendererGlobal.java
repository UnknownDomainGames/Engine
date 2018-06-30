package com.github.unknownstudio.unknowndomain.engine.client.rendering;

import java.util.ArrayList;
import java.util.List;

import com.github.unknownstudio.unknowndomain.engineapi.client.rendering.Renderer;

public class RendererGlobal implements Renderer {
	
	private final List<Renderer> renderers = new ArrayList<>();

	// TODO: hard code.
	private final RendererGame rendererGame;
	private final RendererGui rendererGui;
	
	public RendererGlobal() {
		rendererGame = new RendererGame();
		rendererGui = new RendererGui();
		renderers.add(rendererGame);
		renderers.add(rendererGui);
	}
	
	public List<Renderer> getRenderers() {
		return renderers;
	}
	
	public RendererGame getRendererGame() {
		return rendererGame;
	}

	public RendererGui getRendererGui() {
		return rendererGui;
	}

	@Override
	public void render() {
		for(Renderer renderer : renderers) {
			renderer.render();
		}
	}

}
