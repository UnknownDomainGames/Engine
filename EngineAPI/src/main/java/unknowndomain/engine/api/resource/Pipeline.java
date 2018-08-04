package unknowndomain.engine.api.resource;

public interface Pipeline {
    Pipeline add(String line, Node... node);

    Pipeline subscribe(String line, Endpoint... node);

    <T> T push(String name, Object o) throws Exception;

    interface Context {
        ResourceManager manager();

        <T> T get(String name);

        void emit(String name, Object o);
    }

    interface Endpoint {
        void accept(String source, Object content);
    }

    interface Node {
        Object process(Context context, Object in) throws Exception;
    }
}
