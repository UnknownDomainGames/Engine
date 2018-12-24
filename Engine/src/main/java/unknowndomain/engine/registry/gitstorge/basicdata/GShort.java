package unknowndomain.engine.registry.gitstorge.basicdata;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import unknowndomain.engine.registry.gitstorge.GitStorge;

public class GShort extends GitStorge<GShort> {

    private short value;
    
    public GShort() {
    }
    public GShort(short value) {
        this.value = value;
    }
    public short getValue() {
        return value;
    }
    public void setValue(short value) {
        this.value = value;
    }
    @Override
    public void serialize(DataOutputStream stream) throws IOException {
        super.serialize(stream);
        stream.writeShort(value);
    }

    @Override
    protected void deSerialize(DataInputStream stream) throws IOException {
        value = stream.readShort();
    }

    @Override
    public GShort clone() {
        GShort clone = (GShort)super.clone();
        clone.value = value;
        return clone;
    }

    @Override
    public void gitClone(DataOutputStream stream) throws IOException {
        // TODO
        
    }

}
