package unknowndomain.engine.registry.gitstorge.basicdata;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import unknowndomain.engine.registry.gitstorge.GitStorge;

public class GFloat extends GitStorge<GFloat> {

    private float value;
    
    public GFloat() {
    }
    public GFloat(int value) {
        this.value = value;
    }
    public float getValue() {
        return value;
    }
    public void setValue(float value) {
        this.value = value;
    }
    @Override
    public void serialize(DataOutputStream stream) throws IOException {
        super.serialize(stream);
        stream.writeFloat(value);
    }

    @Override
    protected void deSerialize(DataInputStream stream) throws IOException {
        value = stream.readFloat();
    }

    @Override
    public GFloat clone() {
        GFloat clone = (GFloat)super.clone();
        clone.value = value;
        return clone;
    }

    @Override
    public void gitClone(DataOutputStream stream) throws IOException {
        // TODO
        
    }

}
