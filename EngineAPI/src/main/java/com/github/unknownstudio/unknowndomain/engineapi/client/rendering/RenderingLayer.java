package com.github.unknownstudio.unknowndomain.engineapi.client.rendering;

import java.util.ArrayList;
import java.util.List;

public abstract class RenderingLayer implements Renderer {

	private final List<Renderer> renderers = new ArrayList<>();
	
	protected RenderingLayer() {
	}

	@Override
	public void render() {
		renderers.forEach(r -> r.render());
	}

	public void putRenderer(Renderer renderer) {
		renderers.add(renderer);
	}

	public void removeRenderer(Renderer renderer) {
		renderers.remove(renderer);
	}
}
