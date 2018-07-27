package unknowndomain.engine.client.model;

import java.util.List;
import java.util.stream.Collectors;

public class ModelBlock {
    private List<Face> faces;

    public ModelBlock(){}

    public ModelBlock addFace(Face face){
        faces.add(face);
        return this;
    }

    public List<Face> getFaces(){
        return faces;
    }

    public List<VertexR> getVertices(){
        return faces.stream().flatMap(face -> face.vertices.stream()).collect(Collectors.toList());
    }
}
