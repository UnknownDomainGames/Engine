package unknowndomain.engine.registry.gitstorge.basicarrays;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import unknowndomain.engine.registry.gitstorge.GitStorge;

public class GIntArray extends GitStorge<GIntArray> {
    
    private int[] value;
    
    public GIntArray() {
    }
    public GIntArray(int[] value) {
        this.value = value;
    }
    public int[] getValue() {
        return value;
    }
    public void setValue(int[] value) {
        this.value = value;
    }
    @Override
    public void serialize(DataOutputStream stream) throws IOException {
        super.serialize(stream);
        stream.writeInt(value.length);
        for(int i : value) {
            stream.writeChar(i);
        }
    }

    @Override
    protected void deSerialize(DataInputStream stream) throws IOException {
        if(value != null) {
            return;
        }
        value = new int[stream.readInt()];
        for(int i = 0; i < value.length; i ++) {
            value[i] = stream.readChar();
        }
    }

    @Override
    public GIntArray clone() {
        GIntArray clone = (GIntArray)super.clone();
        clone.value = value.clone();
        return clone;
    }

    @Override
    public void gitClone(DataOutputStream stream) throws IOException {
        // TODO
        
    }

}
