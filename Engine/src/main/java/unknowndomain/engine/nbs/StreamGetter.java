package unknowndomain.engine.nbs;

import unknowndomain.engine.nbs.WriterStream.DataType;

public interface StreamGetter {

    String[] keys();

    void getData(String key, Object data, DataType type);
}
