package nullengine.client.event.rendering;

import nullengine.client.rendering.entity.EntityRenderer;
import nullengine.entity.Entity;
import nullengine.event.Event;

import java.util.function.BiConsumer;

public class RegisterEntityRendererEvent implements Event {

    private final BiConsumer<Class, EntityRenderer> registrationHandler;

    public RegisterEntityRendererEvent(BiConsumer<Class, EntityRenderer> registrationHandler) {
        this.registrationHandler = registrationHandler;
    }

    public <T extends Entity> void register(Class<T> entityType, EntityRenderer<T> renderer) {
        registrationHandler.accept(entityType, renderer);
    }
}
