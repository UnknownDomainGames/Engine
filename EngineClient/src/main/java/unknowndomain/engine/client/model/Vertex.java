package unknowndomain.engine.client.model;

public class Vertex extends VertexR {
    public Vertex(){}

    public Vertex(Vertex former){
        super(former);
    }

    @Override
    public Vertex setVertex(float x, float y, float z){
        this.pos.set(x,y,z);
        return this;
    }

    @Override
    public Vertex setColor(float r, float g, float b, float a){
        this.color.set(r,g,b,a);
        return this;
    }

    @Override
    public Vertex setTexCoord(float u, float v){
        this.texcoord.set(u,v);
        return this;
    }

    @Override
    public Vertex setNormal(float nx, float ny, float nz) {
        normal.set(nx,ny,nz);
        return this;
    }

    @Override
    public Vertex setUsePos(boolean flag){
        usePos = flag;
        return this;
    }
    @Override
    public Vertex setUseColor(boolean flag){
        useColor = flag;
        return this;
    }
    @Override
    public Vertex setUseNormal(boolean flag){
        useNormal = flag;
        return this;
    }
    @Override
    public Vertex setUseTexCoord(boolean flag){
        useTexCoord = flag;
        return this;
    }

    public VertexR asReadonly(){
        return new VertexR(this);
    }
}
