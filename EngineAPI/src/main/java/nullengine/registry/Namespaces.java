package nullengine.registry;

import com.google.common.base.Strings;

public class Namespaces {

    private static String currentNamespace;

    public static String getNamespace() {
        return currentNamespace;
    }

    public static void setNamespace(String namespace) {
        currentNamespace = namespace;
    }

    public static void clearNamespace() {
        currentNamespace = null;
    }

    public static String namespaced(String name) {
        if (name.indexOf(':') != -1) {
            return name;
        }
        return Strings.isNullOrEmpty(currentNamespace) ? name : currentNamespace + ":" + name;
    }
}
