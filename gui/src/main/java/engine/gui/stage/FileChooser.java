package engine.gui.stage;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.MutableStringValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableStringValue;
import engine.gui.internal.GUIPlatform;
import org.apache.commons.lang3.Validate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class FileChooser {

    private MutableStringValue title;
    private MutableObjectValue<Path> initialDirectory;
    private MutableStringValue initialFileName;

    private final ObservableList<ExtensionFilter> extensionFilters = ObservableCollections.observableList(new ArrayList<>());

    public static final class ExtensionFilter {
        private final String description;
        private final List<String> extensions;

        public ExtensionFilter(String description, String... extensions) {
            Validate.noNullElements(extensions);
            this.description = description;
            this.extensions = List.of(extensions);
        }

        public ExtensionFilter(String description, List<String> extensions) {
            Validate.noNullElements(extensions);
            this.description = description;
            this.extensions = List.copyOf(extensions);
        }

        public String getDescription() {
            return description;
        }

        public List<String> getExtensions() {
            return extensions;
        }

        @Override
        public String toString() {
            return "ExtensionFilter{" +
                    "description='" + description + '\'' +
                    ", extensions=" + extensions +
                    '}';
        }
    }

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

    public MutableStringValue initialFileName() {
        if (initialFileName == null) {
            initialFileName = new SimpleMutableStringValue();
        }
        return initialFileName;
    }

    public String getInitialFileName() {
        return initialFileName == null ? null : initialFileName.get();
    }

    public void setInitialFileName(String fileName) {
        initialFileName().set(fileName);
    }

    public ObservableList<ExtensionFilter> getExtensionFilters() {
        return extensionFilters;
    }

    public Path showOpenDialog(Stage ownerStage) {
        return GUIPlatform.getInstance().getFileChooserHelper()
                .showOpenDialog(getTitle(), getInitialDirectory(), getInitialFileName(), getExtensionFilters());
    }

    public List<Path> showOpenMultipleDialog(Stage ownerStage) {
        return GUIPlatform.getInstance().getFileChooserHelper()
                .showOpenMultipleDialog(getTitle(), getInitialDirectory(), getInitialFileName(), getExtensionFilters());
    }

    public Path showSaveDialog(Stage ownerStage) {
        return GUIPlatform.getInstance().getFileChooserHelper()
                .showSaveDialog(getTitle(), getInitialDirectory(), getInitialFileName(), getExtensionFilters());
    }
}
