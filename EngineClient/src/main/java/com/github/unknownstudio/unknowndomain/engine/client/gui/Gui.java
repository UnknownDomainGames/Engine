package com.github.unknownstudio.unknowndomain.engine.client.gui;

import com.github.unknownstudio.unknowndomain.engine.client.gui.panel.GuiPanel;
import com.github.unknownstudio.unknowndomain.engineapi.client.rendering.Renderer;
import com.github.unknownstudio.unknowndomain.engineapi.client.shader.ShaderProgram;

public abstract class Gui implements Renderer {

    public abstract void init();

    public void draw(ShaderProgram shader){
        getPanel().draw(shader);
    }

    /**
     * @override overrided from <code>Renderer</code> and redirected to <code>draw()</code>
     */
    @Override
    public final void render(ShaderProgram shader) {
        draw(shader);
    }

    public abstract GuiPanel getPanel();
}
