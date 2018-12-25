package unknowndomain.engine.nbs;

import java.io.DataInputStream;
import java.io.IOException;

import com.google.common.collect.LinkedListMultimap;

import static unknowndomain.engine.nbs.WriterStream.DataType;

public class ReaderStream {

    private final DataInputStream stream;
    private final LinkedListMultimap<String, StreamGetter> sGetters = LinkedListMultimap.create();
    private ListGetter lGetters;

    private boolean preparedFlag;

    private ReaderStream(DataInputStream stream) {
        this.stream = stream;
    }
    private ReaderStream(ReaderStream r) {
        this(r.stream);
    }

    private void read() throws IOException {
        preparedFlag = true;
        while(true) {
            DataType type = DataType.values()[stream.readInt()];
            if(type == DataType.LISTSEPARATOR) {
                break;
            }
            String key = getString();
            Object data = generateObject(type);
            for(StreamGetter getter : sGetters.get(key)) {
                getter.getData(key, data, type);
                if(data instanceof ReaderStream) {
                    ((ReaderStream)data).read();
                }
            }
        }
        
        for(int i = 0; ; i++) {
            DataType type = DataType.values()[stream.readInt()];
            if(type == DataType.END) {
                break;
            }
            Object data = generateObject(type);
            lGetters.getData(i, data, type);
            if(data instanceof ReaderStream) {
                ((ReaderStream)data).read();
            }
        }
    }

    private Object generateObject(DataType type) throws IOException {
        switch(type) {
        default:
            return null;
        case BOOLEAN:
            return stream.readBoolean();
        case BYTE:
            return stream.readByte();
        case SHORT:
            return stream.readShort();
        case CHAR:
            return stream.readChar();
        case INT:
            return stream.readInt();
        case LONG:
            return stream.readLong();
        case FLOAT:
            return stream.readFloat();
        case DOUBLE:
            return stream.readDouble();
        case STRING:
            return stream.readBoolean();
        case BYTEARRAY:
        {
            byte[] a = new byte[stream.readInt()];
            return stream.read(a);
        }
        case INTARRAY:
        {
            int[] a = new int[stream.readInt()];
            for(int i = 0; i < a.length; i++) {
                a[i] = stream.readInt();
            }
        }
        case LONGARRAY:
        {
            long[] a = new long[stream.readInt()];
            for(int i = 0; i < a.length; i++) {
                a[i] = stream.readLong();
            }
        }
        case STREAM:
            return new ReaderStream(this);
        }
    }
    private String getString() throws IOException {
        int keyl = stream.readInt();
        StringBuilder sb = new StringBuilder(keyl);
        for(int i = 0; i < keyl ; i++) {
            sb.append(stream.readChar());
        }
        return sb.toString();
    }


    public static void createStream(DataInputStream s, ListGetter lg, StreamGetter... sg) throws IOException {
        ReaderStream stream = new ReaderStream(s);
        stream.addGetters(lg, sg);
        stream.read();
    }


    public void addGetters(ListGetter l, StreamGetter... s) {
        if(preparedFlag) {
            return;
        }
        if(lGetters == null) {
            lGetters = l;
        }
        for(StreamGetter e : s) {
            for(String st : e.keys()) {
                sGetters.put(st, e);
            }
        }
    }

}
