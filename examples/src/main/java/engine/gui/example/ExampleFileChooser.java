package engine.gui.example;

import engine.gui.Scene;
import engine.gui.application.GUIApplication;
import engine.gui.control.Button;
import engine.gui.control.TextField;
import engine.gui.layout.FlowPane;
import engine.gui.layout.HBox;
import engine.gui.misc.Border;
import engine.gui.misc.Pos;
import engine.gui.stage.FileChooser;
import engine.gui.stage.Stage;
import engine.util.Color;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExampleFileChooser extends GUIApplication {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FlowPane flowPane = new FlowPane();
        flowPane.alignment().set(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.spacing().set(5);

        TextField textField = new TextField();
        textField.setBorder(new Border(Color.WHITE));
        textField.promptText().set("Enter...");
        textField.getSize().setPrefSize(200, 24);

        Button save = new Button("Save");
        save.setBorder(new Border(Color.WHITE));
        save.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.setInitialFileName("Hello.txt");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text (*.txt)", "*.txt"));
            Path path = fileChooser.showSaveDialog(primaryStage);
            if (path == null) return;
            try {
                Files.writeString(path, textField.text().get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        hBox.getChildren().addAll(textField, save);

        flowPane.getChildren().add(hBox);

        Scene scene = new Scene(flowPane);

        primaryStage.setScene(scene);
        primaryStage.setSize(300, 200);
        primaryStage.setTitle("Example File Chooser");
        primaryStage.show();
    }
}
