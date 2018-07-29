package unknowndomain.engine.client.rendering;

import java.util.ArrayList;
import java.util.List;

import unknowndomain.engine.api.client.display.Camera;
import unknowndomain.engine.api.client.rendering.RenderingLayer;

public class RendererGlobal {

    private final List<RenderingLayer> renderers = new ArrayList<>();
    private Camera camera;

    public RendererGlobal() {
//		rendererGame = new RendererGame();
//		rendererGui = new RendererGui();
        renderers.add(new RenderCommon());
//		renderers.add(rendererGui);
    }

    // TODO: hard code.
//	private final RendererGame rendererGame;
//	private final RendererGui rendererGui;

    public Camera getCamera() {
        return camera;
    }

    public List<RenderingLayer> getRenderers() {
        return renderers;
    }

//	public RendererGame getRendererGame() {
//		return rendererGame;
//	}

//	public RendererGui getRendererGui() {
//		return rendererGui;
//	}

    public void render() {
        for (RenderingLayer renderer : renderers) {
            renderer.render();
        }
    }

}
