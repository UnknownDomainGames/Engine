package engine.gui.example;

import engine.gui.Scene;
import engine.gui.application.GUIApplication;
import engine.gui.layout.FlowPane;
import engine.gui.layout.HBox;
import engine.gui.misc.Pos;
import engine.gui.stage.Stage;
import engine.gui.text.Text;

import java.io.IOException;
import java.nio.file.Files;

public class ExampleDropFile extends GUIApplication {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FlowPane flowPane = new FlowPane();
        flowPane.alignment().set(Pos.CENTER);
        flowPane.setPrefSize(300, 200);

        HBox hBox = new HBox();
        hBox.spacing().set(5);

        Text text = new Text("Drop file...");
        flowPane.setOnDrop(event -> {
            try {
                text.setText(Files.readString(event.getPaths().get(0)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        hBox.getChildren().add(text);
        flowPane.getChildren().add(hBox);

        primaryStage.setScene(new Scene(flowPane));
        primaryStage.setTitle("Example Drop File");
        primaryStage.show();
    }
}
