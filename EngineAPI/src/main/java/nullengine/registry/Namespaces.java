package nullengine.registry;

@Deprecated
public class Namespaces {

    private static String currentNamespace;

    public static String getNamespace() {
        return currentNamespace;
    }

    public static void setNamespace(String namespace) {
        currentNamespace = namespace;
    }
}
