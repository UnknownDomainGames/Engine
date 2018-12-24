package unknowndomain.engine.registry.gitstorge.basicarrays;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import unknowndomain.engine.registry.gitstorge.GitStorge;

public class GByteArray extends GitStorge<GByteArray> {
    
    private byte[] value;
    
    public GByteArray() {
    }
    public GByteArray(byte[] value) {
        this.value = value;
    }
    public byte[] getValue() {
        return value;
    }
    public void setValue(byte[] value) {
        this.value = value;
    }
    @Override
    public void serialize(DataOutputStream stream) throws IOException {
        super.serialize(stream);
        stream.writeInt(value.length);
        stream.write(value);
    }

    @Override
    protected void deSerialize(DataInputStream stream) throws IOException {
        if(value == null) {
            value = new byte[stream.readInt()];
        }
        stream.read(value);
    }

    @Override
    public GByteArray clone() {
        GByteArray clone = (GByteArray)super.clone();
        clone.value = value.clone();
        return clone;
    }

    @Override
    public void gitClone(DataOutputStream stream) throws IOException {
        // TODO
        
    }

}
