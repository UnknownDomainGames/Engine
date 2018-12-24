package unknowndomain.engine.registry.gitstorge.basicdata;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import unknowndomain.engine.registry.gitstorge.GitStorge;

public class GBoolean extends GitStorge<GInteger> {

    private boolean value;
    
    public GBoolean() {
    }
    public GBoolean(boolean value) {
        this.value = value;
    }
    public boolean getValue() {
        return value;
    }
    public void setValue(boolean value) {
        this.value = value;
    }
    @Override
    public void serialize(DataOutputStream stream) throws IOException {
        super.serialize(stream);
        stream.writeBoolean(value);
    }

    @Override
    protected void deSerialize(DataInputStream stream) throws IOException {
        value = stream.readBoolean();
    }

    @Override
    public GBoolean clone() {
        GBoolean clone = (GBoolean)super.clone();
        clone.value = value;
        return clone;
    }

    @Override
    public void gitClone(DataOutputStream stream) throws IOException {
        // TODO
        
    }

}
