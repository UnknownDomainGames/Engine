package unknowndomain.engine.client.resource;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class Pipeline {
    private ResourceManager manager;
    private Map<String, Pair<List<Node>, List<Endpoint>>> pipes = new HashMap<>();

    public Pipeline(ResourceManager manager) {
        this.manager = manager;
    }

    public Pipeline add(String line, Node... node) {
        if (node == null || node.length == 0) return this;
        List<Node> nodes = pipes.computeIfAbsent(line, (k) -> Pair.of(new ArrayList<>(), new ArrayList<>())).getLeft();
        nodes.addAll(Arrays.asList(node));
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> Map<String, Object> push(String name, Object o) throws Exception {
        List<Exception> exceptions = new ArrayList<>();
        Map<String, Object> cached = new HashMap<>();
        Context context = new Context() {
            @Override
            public ResourceManager manager() {
                return manager;
            }

            @Override
            public <T> T get(String name) {
                return (T) cached.get(name);
            }

            @Override
            public void emit(String name, Object o) {
                Pair<List<Node>, List<Endpoint>> pair = pipes.get(name);
                if (pair != null) {
                    if (pair.getLeft().size() != 0) {
                        try {
                            Object pack = o;
                            for (Node node : pair.getLeft()) pack = node.process(this, pack);
                            cached.put(name, pack);
                            final Object result = pack;
                            List<Endpoint> right = pair.getRight();
                            if (right != null && pack != null) right.forEach(e -> e.accept(name, result));
                        } catch (Exception e) {
                            exceptions.add(e);
                        }
                    } else if (pair.getRight().size() != 0) {
                        pair.getRight().forEach(e -> e.accept(name, o));
                    }
                } else {
                    cached.put(name, o);
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
        return cached;
    }

    public interface Context {
        ResourceManager manager();

        <T> T get(String name);

        void emit(String name, Object o);
    }

    public interface Endpoint {
        void accept(String source, Object content);
    }

    public interface Node {
        Object process(Context context, Object in) throws Exception;
    }
}
