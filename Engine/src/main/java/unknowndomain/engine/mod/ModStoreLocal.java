package unknowndomain.engine.mod;

import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;

import unknowndomain.engine.util.Transfer;

public class ModStoreLocal implements ModStore {
    /**
     * Since we have already decide all the mod will be downloaded to local.
     * <p>The loader could confirm that it can only load from disk</p>
     * <p>Otherwise, we will have a much pure api: (InputStream)->ModContainer</p>
     */
    private final Path root;

    public ModStoreLocal(Path root) {
        this.root = root;
    }

    /**
     * There might be different structure to store mod.
     */
    protected Path resolve(ModIdentifier identifier) {
        return root.resolve(identifier.getGroup()).resolve(identifier.getId() + "-" + identifier.getVersion() + ".jar");
    }

    @Override
    public boolean exists(@NonNull ModIdentifier identifier) {
        Path source = resolve(Validate.notNull(identifier));
        return Files.exists(source);
    }

    @Override
    public void store(@Nonnull ModIdentifier identifier, InputStream stream) {
        Path resolve = resolve(identifier);
        try {
            long size = identifier instanceof ModRepository.RemoteModMetadata ? ((ModRepository.RemoteModMetadata) identifier).getSize() : -1;
            Transfer transfer = new Transfer(Channels.newChannel(stream), FileChannel.open(resolve, StandardOpenOption.WRITE), size);
            transfer.call(); // TODO: monitor transfer
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public Path path(ModIdentifier identifier) {
		return resolve(identifier);
	}
}
