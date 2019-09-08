package nullengine.client.rendering;


/**
 * The base renderer for the game. A Mod need to have this if it want to render new things...
 */
public interface Renderer {

    void init(RenderManager context);

    void render(float partial);

    void dispose();
}
