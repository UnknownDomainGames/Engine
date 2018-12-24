package unknowndomain.engine.registry.gitstorge.basicarrays;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import unknowndomain.engine.registry.gitstorge.GitStorge;

public class GLongArray extends GitStorge<GLongArray> {
    
    private long[] value;
    
    public GLongArray() {
    }
    public GLongArray(long[] value) {
        this.value = value;
    }
    public long[] getValue() {
        return value;
    }
    public void setValue(long[] value) {
        this.value = value;
    }
    @Override
    public void serialize(DataOutputStream stream) throws IOException {
        super.serialize(stream);
        stream.writeInt(value.length);
        for(long l : value) {
            stream.writeLong(l);
        }
    }

    @Override
    protected void deSerialize(DataInputStream stream) throws IOException {
        if(value != null) {
            return;
        }
        value = new long[stream.readInt()];
        for(int i = 0; i < value.length; i ++) {
            value[i] = stream.readLong();
        }
    }

    @Override
    public GLongArray clone() {
        GLongArray clone = (GLongArray)super.clone();
        clone.value = value.clone();
        return clone;
    }

    @Override
    public void gitClone(DataOutputStream stream) throws IOException {
        // TODO
        
    }

}
