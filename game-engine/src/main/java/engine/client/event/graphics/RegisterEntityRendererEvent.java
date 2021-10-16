package engine.client.event.graphics;

import engine.entity.Entity;
import engine.event.Event;
import engine.graphics.entity.EntityRenderer;

import java.util.function.BiConsumer;

@SuppressWarnings("rawtypes")
public class RegisterEntityRendererEvent implements Event {

    private final BiConsumer<Class, EntityRenderer> registrationHandler;

    public RegisterEntityRendererEvent(BiConsumer<Class, EntityRenderer> registrationHandler) {
        this.registrationHandler = registrationHandler;
    }

    public <T extends Entity> void register(Class<T> entityType, EntityRenderer<T> renderer) {
        registrationHandler.accept(entityType, renderer);
    }
}
