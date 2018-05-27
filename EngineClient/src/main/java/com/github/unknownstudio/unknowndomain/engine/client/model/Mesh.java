package com.github.unknownstudio.unknowndomain.engine.client.model;

import org.joml.Vector3f;

import java.util.List;

public class Mesh {
    private List<? extends VertexR> vertices;
    private Vector3f color;
    public Mesh(List<? extends VertexR> vertices){
        this.vertices = vertices;
    }
}
