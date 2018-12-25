package unknowndomain.engine.nbs;

import java.io.IOException;

public interface ListSetter {

    int length();

    void setData(WriterStream w, int i) throws IOException;
}
