package unknowndomain.engine.nbs;

import unknowndomain.engine.nbs.WriterStream.DataType;

public interface ListGetter {

    void getData(int index, Object data, DataType type);
}
