package unknowndomain.engine.client.rendering.registry;

import javax.annotation.Nonnull;

public interface RenderingRegistry<KEY, ENTRY> {

    ENTRY register(@Nonnull KEY key, @Nonnull ENTRY obj);

    ENTRY getValue(@Nonnull KEY key);
}
