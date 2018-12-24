package unknowndomain.engine.registry.gitstorge;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.registry.gitstorge.GitStorge.Type;
import unknowndomain.engine.registry.gitstorge.basicdata.GInteger;

public abstract class GitStorge<T extends GitStorge<T>> {
    
    public enum Attribute{
        READONLY(1), COPY(2), FINAL(4);
        
        public final int code;
        
        private Attribute(int code) {
            this.code = code;
        }
        
        public boolean haveAttribute(int code) {
            return (code & this.code) != 0;
        }
        
        public static int generateAttribute(Attribute... attrs) {
            int iattr = 0;
            for(Attribute attr : attrs) {
                iattr += attr.code;
            }
            return iattr;
        }
    }
    
    public enum Type{
        INTEGER(GInteger.class);
        
        public final Class<? extends GitStorge<?>> type;
        
        private Type(Class<? extends GitStorge<?>> type) {
            this.type = type;
        }
        
        public GitStorge<?> create (int attr) {
            try {
                GitStorge<?> val = type.newInstance();
                val.setup(this, attr);
                return val;
            } catch (InstantiationException | IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }
    
    public enum Head{
        PRESSED(1), GITCLONE(2);
        
        public final int code;
        
        private Head(int code) {
            this.code = code;
        }
        
        public boolean haveAttribute(int code) {
            return (code & this.code) != 0;
        }
        
        public static int generateAttribute(Head... attrs) {
            int iattr = 0;
            for(Head attr : attrs) {
                iattr += attr.code;
            }
            return iattr;
        }
    }
    
    
    
    
    public static GitStorge<?> read(InputStream stream) throws IOException {
        int head = stream.read();
        if(Head.PRESSED.haveAttribute(head)) {
            stream = new GZIPInputStream(stream);
        }
        stream = new DataInputStream(stream);
        GitStorge<?> val = Type.values()[stream.read()].create(stream.read());
        val.deSerialize((DataInputStream) stream);
        return val;
    }
    
    public static void write(GitStorge<?> g, OutputStream stream, int head) throws IOException {
        if(Head.PRESSED.haveAttribute(head)) {
            stream = new GZIPOutputStream(stream);
        }
        stream = new DataOutputStream(stream);
        g.serialize((DataOutputStream) stream);
    }
    
    
    
    
    private int attr;
    private Type type;
    
    public void setup(Type type, int attr) {
        this.type = type;
        this.attr = attr;
    }
    
    public int getAttribute() {
        return attr;
    }
    
    public boolean haveAttribute(Attribute attr) {
        return attr.haveAttribute(this.attr);
    }
    
    public Type getType() {
        return type;
    }
    
    public void serialize(DataOutputStream stream) throws IOException{
        stream.write(getType().ordinal());
        stream.write(getAttribute());
    }
    
    protected abstract void deSerialize(DataInputStream stream) throws IOException;
    
    public GitStorge<?> clone() {
        return type.create(attr);
    }
    
    public void gitClone(DataOutputStream stream) throws IOException {
        //TODO
    }
}
