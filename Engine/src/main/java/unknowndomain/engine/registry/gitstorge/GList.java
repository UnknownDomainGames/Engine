package unknowndomain.engine.registry.gitstorge;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class GList extends GitStorge<GList> {

    private ArrayList<GitStorge<?>> value;
    
    public GList() {
    }
    public GList(ArrayList<GitStorge<?>> value) {
        this.value = value;
    }
    @Override
    public void serialize(DataOutputStream stream) throws IOException {
        super.serialize(stream);
        stream.writeInt(value.size());
        for(GitStorge<?> g : value) {
            g.serialize(stream);
        }
    }

    @Override
    protected void deSerialize(DataInputStream stream) throws IOException {
        value = new ArrayList<>(stream.readInt());
        for(int i = 0; i < value.size(); i ++) {
            Type.deSerialize(stream.read(), stream.read()).deSerialize(stream);
        }
    }

    @Override
    public GList clone() {
        GList clone = (GList)super.clone();
        ArrayList<GitStorge<?>> cl = new ArrayList<>(value.size());
        for(GitStorge<?> g : value) {
            cl.add(g.clone());
        }
        clone.value = cl;
        return clone;
    }

    @Override
    public void gitClone(DataOutputStream stream) throws IOException {
        // TODO
        
    }

}
