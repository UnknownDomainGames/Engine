package unknowndomain.engine.registry.gitstorge.basicdata;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import unknowndomain.engine.registry.gitstorge.GitStorge;

public class GCharacter extends GitStorge<GCharacter> {

    private char value;
    
    public GCharacter() {
    }
    public GCharacter(char value) {
        this.value = value;
    }
    public char getValue() {
        return value;
    }
    public void setValue(char value) {
        this.value = value;
    }
    @Override
    public void serialize(DataOutputStream stream) throws IOException {
        super.serialize(stream);
        stream.writeChar(value);
    }

    @Override
    protected void deSerialize(DataInputStream stream) throws IOException {
        value = stream.readChar();
    }

    @Override
    public GCharacter clone() {
        GCharacter clone = (GCharacter)super.clone();
        clone.value = value;
        return clone;
    }

    @Override
    public void gitClone(DataOutputStream stream) throws IOException {
        // TODO
        
    }

}
