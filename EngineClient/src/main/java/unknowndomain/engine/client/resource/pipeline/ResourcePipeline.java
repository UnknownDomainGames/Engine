package unknowndomain.engine.client.resource.pipeline;

import unknowndomain.engine.api.resource.ResourceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcePipeline {
    private Map<String, List<Node>> pipes = new HashMap<>();

    private ResourceManager manager;

    public ResourcePipeline(ResourceManager manager) {
        this.manager = manager;
    }

    public ResourcePipeline add(String line, Node node) {
        List<Node> nodes = pipes.computeIfAbsent(line, (k) -> new ArrayList<>());
        nodes.add(node);
        return this;
    }

    public void push(String name, Object o) throws Exception {
        List<Exception> exceptions = new ArrayList<>();
        Map<String, Object> cached = new HashMap<>();
        Context context = new Context() {

            @Override
            public ResourceManager manager() {
                return manager;
            }

            @Override
            @SuppressWarnings("unchecked")
            public <T> T get(String name) {
                return (T) cached.get(name);
            }

            @Override
            public void emit(String name, Object o) {
                List<Node> nodes = pipes.get(name);
                if (nodes != null && nodes.size() != 0) {
                    try {
                        Object pack = o;
                        for (Node node : nodes) {
                            pack = node.process(this, pack);
                        }
                        cached.put(name, pack);
                    } catch (Exception e) {
                        exceptions.add(e);
                    }
                }
            }
        };
        context.emit(name, o);

        if (exceptions.size() != 0) {
            Exception ex = new Exception("Some errors occurred when we process the " + name);
            for (Exception e : exceptions) {
                ex.addSuppressed(e);
            }
            throw ex;
        }
    }

    public interface Context {
        ResourceManager manager();

        <T> T get(String name);

        void emit(String name, Object o);
    }

    public interface Node {
        Object process(Context context, Object in) throws Exception;
    }
}
