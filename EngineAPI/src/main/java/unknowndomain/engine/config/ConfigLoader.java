package unknowndomain.engine.config;

public interface ConfigLoader {

    Config loadConfig(String path, Serializer serializer);

}
