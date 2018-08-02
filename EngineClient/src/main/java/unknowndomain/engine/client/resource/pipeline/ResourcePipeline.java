package unknowndomain.engine.client.resource.pipeline;

import unknowndomain.engine.api.resource.ResourceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcePipeline {
    private Map<String, Node> nodeMap = new HashMap<>();
    private ResourceManager manager;

    public ResourcePipeline(ResourceManager manager) {
        this.manager = manager;
    }

    public ResourcePipeline add(String name, Node node) {
        nodeMap.put(name, node);
        return this;
    }

    public void push(String name, Object o) throws Exception {
        List<Exception> exceptions = new ArrayList<>();
        Context context = new Context() {
            Map<String, Object> cached = new HashMap<>();

            {
                cached.put(name, o);
            }

            @Override
            public ResourceManager manager() {
                return manager;
            }

            @Override
            @SuppressWarnings("unchecked")
            public <T> T in(String name) {
                return (T) cached.get(name);
            }

            @Override
            public void out(String name, Object o) {
                cached.put(name, o);
                Node node = nodeMap.get(name);
                if (node != null) {
                    try {
                        node.process(this);
                    } catch (Exception e) {
                        exceptions.add(e);
                    }
                }
            }
        };
        context.out(name, o);
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

        <T> T in(String name);

        void out(String name, Object o);
    }

    public interface Node {
        void process(Context context) throws Exception;
    }
}
