package com.github.unknownstudio.unknowndomain.engine.client.rendering;

import java.util.ArrayList;
import java.util.List;

import com.github.unknownstudio.unknowndomain.engineapi.client.rendering.Renderer;
import com.github.unknownstudio.unknowndomain.engineapi.client.rendering.RenderingLayer;
import com.github.unknownstudio.unknowndomain.engineapi.client.shader.ShaderProgram;

public class RendererGlobal {
	
	private final List<RenderingLayer> renderers = new ArrayList<>();

	// TODO: hard code.
	private final RendererGame rendererGame;
	private final RendererGui rendererGui;
	
	public RendererGlobal() {
		rendererGame = new RendererGame();
		rendererGui = new RendererGui();
		renderers.add(rendererGame);
		renderers.add(rendererGui);
	}
	
	public List<RenderingLayer> getRenderers() {
		return renderers;
	}
	
	public RendererGame getRendererGame() {
		return rendererGame;
	}

	public RendererGui getRendererGui() {
		return rendererGui;
	}

	public void render() {
		for(RenderingLayer renderer : renderers) {
			renderer.render();
		}
	}

}
