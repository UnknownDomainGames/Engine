package unknowndomain.engine.config;

import java.nio.channels.Channel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map;

public interface Serializer {
    Map<Object,Object> load(ReadableByteChannel channel);

    void save(WritableByteChannel channel);
}
