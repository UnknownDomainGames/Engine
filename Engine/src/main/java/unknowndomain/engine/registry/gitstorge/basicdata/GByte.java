package unknowndomain.engine.registry.gitstorge.basicdata;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import unknowndomain.engine.registry.gitstorge.GitStorge;

public class GByte extends GitStorge<GByte> {

    private byte value;
    
    public GByte() {
    }
    public GByte(byte value) {
        this.value = value;
    }
    public byte getValue() {
        return value;
    }
    public void setValue(byte value) {
        this.value = value;
    }
    @Override
    public void serialize(DataOutputStream stream) throws IOException {
        super.serialize(stream);
        stream.writeByte(value);
    }

    @Override
    protected void deSerialize(DataInputStream stream) throws IOException {
        value = stream.readByte();
    }

    @Override
    public GByte clone() {
        GByte clone = (GByte)super.clone();
        clone.value = value;
        return clone;
    }

    @Override
    public void gitClone(DataOutputStream stream) throws IOException {
        // TODO
        
    }

}
