package engine.gui.internal;

import engine.gui.stage.FileChooser;

import java.nio.file.Path;
import java.util.List;

public interface FileChooserHelper {

    Path showOpenDialog(String title, Path initialDirectory, String initialFileName, List<FileChooser.ExtensionFilter> extensionFilters);

    List<Path> showOpenMultipleDialog(String title, Path initialDirectory, String initialFileName, List<FileChooser.ExtensionFilter> extensionFilters);

    Path showSaveDialog(String title, Path initialDirectory, String initialFileName, List<FileChooser.ExtensionFilter> extensionFilters);

    Path showSelectDirectoryDialog(String title, Path initialDirectory);
}
