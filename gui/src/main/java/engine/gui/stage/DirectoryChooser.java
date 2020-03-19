package engine.gui.stage;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.MutableStringValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableStringValue;
import engine.gui.internal.GUIPlatform;

import java.nio.file.Files;
import java.nio.file.Path;

public final class DirectoryChooser {

    private MutableStringValue title;
    private MutableObjectValue<Path> initialDirectory;

    public MutableStringValue title() {
        if (title == null) {
            title = new SimpleMutableStringValue();
        }
        return title;
    }

    public String getTitle() {
        return title == null ? null : title.get();
    }

    public void setTitle(String title) {
        title().set(title);
    }

    public MutableObjectValue<Path> initialDirectory() {
        if (initialDirectory == null) {
            initialDirectory = new SimpleMutableObjectValue<>() {
                @Override
                public void set(Path value) {
                    if (!Files.isDirectory(value))
                        throw new IllegalArgumentException("Cannot set initial directory as file");
                    super.set(value);
                }
            };
        }
        return initialDirectory;
    }

    public Path getInitialDirectory() {
        return initialDirectory == null ? null : initialDirectory.get();
    }

    public void setInitialDirectory(Path directory) {
        initialDirectory().set(directory);
    }

    public Path show(Stage ownerStage) {
        return GUIPlatform.getInstance().getFileChooserHelper()
                .showSelectDirectoryDialog(getTitle(), getInitialDirectory());
    }
}
