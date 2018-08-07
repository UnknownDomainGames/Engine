package unknowndomain.engine;

public class Loader {
    public static void init() {
        try {
            Class.forName("unknowndomain.engine.action.ActionBuilderImpl");
            Class.forName("unknowndomain.engine.event.AsmEventBus");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}