package unknowndomain.engine.registry.gitstorge.basicdata;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import unknowndomain.engine.registry.gitstorge.GitStorge;

public class GString extends GitStorge<GString> {

    private String value;
    
    public GString() {
    }
    public GString(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    @Override
    public void serialize(DataOutputStream stream) throws IOException {
        super.serialize(stream);
        stream.writeInt(value.length());
        stream.writeChars(value);
    }

    @Override
    protected void deSerialize(DataInputStream stream) throws IOException {
        if(value != null) {
            return;
        }
        char[] c = new char[stream.readInt()];
        for(int i = 0; i < c.length; i ++) {
            c[i] = stream.readChar();
        }
        value = new String(c);
    }

    @Override
    public GString clone() {
        GString clone = (GString)super.clone();
        clone.value = value;
        return clone;
    }

    @Override
    public void gitClone(DataOutputStream stream) throws IOException {
        // TODO
        
    }

}
