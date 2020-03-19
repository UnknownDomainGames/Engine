package engine.gui.internal.impl.lwjgl;

import engine.gui.internal.FileChooserHelper;
import engine.gui.stage.FileChooser;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.util.tinyfd.TinyFileDialogs.*;

public class TinyFDFileChooserHelper implements FileChooserHelper {

    @Override
    public Path showOpenDialog(String title, Path initialDirectory, String initialFileName, List<FileChooser.ExtensionFilter> extensionFilters) {
        String result = showDialog(title, initialDirectory, initialFileName, extensionFilters, true, false);
        return result == null ? null : Path.of(result);
    }

    @Override
    public List<Path> showOpenMultipleDialog(String title, Path initialDirectory, String initialFileName, List<FileChooser.ExtensionFilter> extensionFilters) {
        String result = showDialog(title, initialDirectory, initialFileName, extensionFilters, true, true);
        return result == null ? null : Stream.of(result.split("\\|")).map(Path::of).collect(Collectors.toList());
    }

    private String showDialog(String title, Path initialDirectory, String initialFileName, List<FileChooser.ExtensionFilter> extensionFilters, boolean isOpen, boolean allowMultipleSelect) {
        String initialPath = null;
        if (initialDirectory != null) {
            initialPath = initialDirectory.toAbsolutePath().toString();
        }
        if (initialFileName != null) {
            if (initialPath == null) initialPath = initialFileName;
            else initialPath = initialPath + File.separator + initialFileName;
        }

        if (extensionFilters.size() >= 1) {
            MemoryStack stack = stackGet();
            int stackPointer = stack.getPointer();
            try {
                var extensionFilter = extensionFilters.get(0);
                var extensions = extensionFilter.getExtensions();
                var extensionPointers = BufferUtils.createPointerBuffer(extensions.size());
                for (var extension : extensions) {
                    stack.nUTF8Safe(extension, true);
                    extensionPointers.put(stack.getPointerAddress());
                }
                extensionPointers.flip();
                return isOpen ? tinyfd_openFileDialog(title, initialPath,
                        extensionPointers, extensionFilter.getDescription(), allowMultipleSelect) :
                        tinyfd_saveFileDialog(title, initialPath, extensionPointers, extensionFilter.getDescription());
            } finally {
                stack.setPointer(stackPointer);
            }
        } else {
            return isOpen ? tinyfd_openFileDialog(title, initialPath,
                    null, null, allowMultipleSelect) :
                    tinyfd_saveFileDialog(title, initialPath, null, null);
        }
    }

    @Override
    public Path showSaveDialog(String title, Path initialDirectory, String initialFileName, List<FileChooser.ExtensionFilter> extensionFilters) {
        String result = showDialog(title, initialDirectory, initialFileName, extensionFilters, false, false);
        return result == null ? null : Path.of(result);
    }

    @Override
    public Path showSelectDirectoryDialog(String title, Path initialDirectory) {
        String result = tinyfd_selectFolderDialog(title, initialDirectory == null ? null : initialDirectory.toAbsolutePath().toString());
        return result == null ? null : Path.of(result);
    }
}
