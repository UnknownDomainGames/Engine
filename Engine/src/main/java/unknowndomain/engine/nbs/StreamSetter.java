package unknowndomain.engine.nbs;

import java.io.IOException;

public interface StreamSetter {

    String[] keys();

    void setData(WriterStream w,String key) throws IOException;
}
