package unknowndomain.engine.client.model;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

/**
 * read-only vertex
 */
public class VertexR {
    protected Vector3f pos;
    protected Vector4f color;
    protected Vector3f normal;
    protected Vector2f texcoord;

    protected boolean usePos;
    protected boolean useColor;
    protected boolean useNormal;
    protected boolean useTexCoord;

    public VertexR(){}

    public VertexR(Vertex former){
        this.pos = new Vector3f(former.pos);
        this.color = new Vector4f(former.color);
        this.normal = new Vector3f(former.normal);
        this.texcoord = new Vector2f(former.texcoord);
        this.usePos = former.usePos;
        this.useColor = former.useColor;
        this.useNormal = former.useNormal;
        this.useTexCoord = former.useTexCoord;
    }

    public VertexR setVertex(float x, float y, float z){
        return this;
    }

    public VertexR setColor(float r, float g, float b, float a){
        return this;
    }

    public VertexR setNormal(float nx, float ny, float nz){
        return this;
    }

    public VertexR setTexCoord(float u, float v){
        return this;
    }

    public VertexR setUsePos(boolean flag){
        return this;
    }
    public VertexR setUseColor(boolean flag){
        return this;
    }
    public VertexR setUseNormal(boolean flag){
        return this;
    }
    public VertexR setUseTexCoord(boolean flag){
        return this;
    }

    public boolean isUsePos() {
        return usePos;
    }

    public boolean isUseColor() {
        return useColor;
    }

    public boolean isUseNormal() {
        return useNormal;
    }

    public boolean isUseTexCoord() {
        return useTexCoord;
    }
}
