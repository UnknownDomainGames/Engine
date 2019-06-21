package unknowndomain.engine.client.asset.source;

import unknowndomain.engine.mod.ModContainer;

import javax.annotation.Nonnull;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class ModAssetSource extends CompositeAssetSource {

    public static ModAssetSource create(@Nonnull ModContainer modContainer) throws IOException {
        return new ModAssetSource(requireNonNull(modContainer), "assets");
    }

    private final ModContainer modContainer;

    private ModAssetSource(ModContainer modContainer, String root) throws IOException {
        super(modContainer.getSources(), root, modContainer.getClassLoader());
        this.modContainer = modContainer;
    }

    public ModContainer getMod() {
        return modContainer;
    }
}
