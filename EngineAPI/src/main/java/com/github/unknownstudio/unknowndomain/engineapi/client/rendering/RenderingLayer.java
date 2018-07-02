package com.github.unknownstudio.unknowndomain.engineapi.client.rendering;

import com.github.unknownstudio.unknowndomain.engineapi.client.shader.ShaderProgram;

import java.util.ArrayList;
import java.util.List;

public interface RenderingLayer extends Renderer {

    List<Renderer> renderer = new ArrayList<>();

    void render();

    @Override
    default void render(ShaderProgram shader){
        renderer.forEach(r -> r.render(shader));
    }

    default void putRenderer(Renderer renderer){
        this.renderer.add(renderer);
    }

    default void removeRenderer(Renderer renderer){
        this.renderer.remove(renderer);
    }
}
