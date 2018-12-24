package unknowndomain.engine.registry.gitstorge.basicarrays;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import unknowndomain.engine.registry.gitstorge.GitStorge;

public class GCharArray extends GitStorge<GCharArray> {
    
private char[] value;
    
    public GCharArray() {
    }
    public GCharArray(char[] value) {
        this.value = value;
    }
    public char[] getValue() {
        return value;
    }
    public void setValue(char[] value) {
        this.value = value;
    }
    @Override
    public void serialize(DataOutputStream stream) throws IOException {
        super.serialize(stream);
        stream.writeInt(value.length);
        for(char c : value) {
            stream.writeChar(c);
        }
    }

    @Override
    protected void deSerialize(DataInputStream stream) throws IOException {
        if(value != null) {
            return;
        }
        value = new char[stream.readInt()];
        for(int i = 0; i < value.length; i ++) {
            value[i] = stream.readChar();
        }
    }

    @Override
    public GCharArray clone() {
        GCharArray clone = (GCharArray)super.clone();
        clone.value = value.clone();
        return clone;
    }

    @Override
    public void gitClone(DataOutputStream stream) throws IOException {
        // TODO
        
    }

}
