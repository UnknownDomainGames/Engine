package unknowndomain.engine.nbs;

import java.io.DataOutputStream;
import java.io.IOException;

public class WriterStream {

    public enum DataType{
        BOOLEAN, BYTE, SHORT, CHAR, INT, LONG, FLOAT, DOUBLE, STRING,//basic data
        BYTEARRAY, INTARRAY, LONGARRAY,//basic array
        STREAM, LISTSEPARATOR, END//special data
    }

    private final DataOutputStream stream;

    private final  StreamSetter[] sSetters;
    private final ListSetter lSetter;
    private boolean listFlag;
    
    private String key;

    private WriterStream(DataOutputStream stream, ListSetter lSetter, StreamSetter... cSetters) {
        this.stream = stream;
        this.sSetters = cSetters;
        this.lSetter = lSetter;
    }

    private WriterStream(WriterStream w, ListSetter lSetter, StreamSetter... cSetters) {
        this(w.stream, lSetter, cSetters);
    }

    private void write() throws IOException {
        for(StreamSetter c : sSetters) {
            for(String s : c.keys()) {
                key = s;
                c.setData(this, s);
            }
        }
        key = null;
        stream.writeInt(DataType.LISTSEPARATOR.ordinal());
        listFlag = true;
        for(int i = 0; i < lSetter.length(); i++) {
            lSetter.setData(this, i);
        }
        stream.writeInt(DataType.END.ordinal());
    }
    private void addHead(DataType type) throws IOException {
        stream.writeByte(type.ordinal());
        if(!listFlag) {
            stream.writeInt(key.length());
            stream.writeChars(key);
        }
    }

    //********************************************

    public static void createStream(DataOutputStream s, ListSetter lSetter, StreamSetter... cSetters) throws IOException {
        new WriterStream(s, lSetter, cSetters).write();
    }

    //********************************************

    public void putBoolean(boolean value) throws IOException {
        addHead(DataType.BOOLEAN);
        stream.writeBoolean(value);
    }
    public void putByte(byte value) throws IOException {
        addHead(DataType.BYTE);
        stream.writeByte(value);
    }
    public void putShort(String key, short value) throws IOException {
        addHead(DataType.SHORT);
        stream.writeShort(value);
    }
    public void putInt(String key, int value) throws IOException {
        addHead(DataType.INT);
        stream.writeInt(value);
    }
    public void putLong(String key, long value) throws IOException {
        addHead(DataType.LONG);
        stream.writeLong(value);
    }
    public void putFloat(String key, float value) throws IOException {
        addHead(DataType.FLOAT);
        stream.writeFloat(value);
    }
    public void putDouble(String key, double value) throws IOException {
        addHead(DataType.DOUBLE);
        stream.writeDouble(value);
    }
    public void putString(String key, String value) throws IOException {
        addHead(DataType.STRING);
        stream.writeInt(value.length());
        stream.writeChars(value);
    }

    //********************************************

    public void putByteArray(byte[] value) throws IOException {
        addHead(DataType.BYTEARRAY);
        stream.writeInt(value.length);
        stream.write(value);
    }
    public void putIntArray(int[] value) throws IOException {
        addHead(DataType.INTARRAY);
        stream.writeInt(value.length);
        for(int i : value) {
            stream.writeInt(i);
        }
    }
    public void putLongArray(long[] value) throws IOException {
        addHead(DataType.LONGARRAY);
        stream.writeInt(value.length);
        for(long i : value) {
            stream.writeLong(i);
        }
    }

    //********************************************

    public void putStream(ListSetter lSetter, StreamSetter... cSetters) throws IOException {
        addHead(DataType.STREAM);
        WriterStream w = new WriterStream(this, lSetter, cSetters);
        w.write();
    }
}
