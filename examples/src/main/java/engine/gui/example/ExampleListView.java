package engine.gui.example;

import engine.gui.Region;
import engine.gui.Scene;
import engine.gui.application.GUIApplication;
import engine.gui.control.ListView;
import engine.gui.layout.FlowPane;
import engine.gui.misc.Pos;
import engine.gui.stage.Stage;

public class ExampleListView extends GUIApplication {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FlowPane flowPane = new FlowPane();
        flowPane.alignment().set(Pos.CENTER);

        ListView<String> listView = new ListView<>();
        listView.setPrefSize(Region.USE_PARENT_VALUE, Region.USE_PARENT_VALUE);
        for (int i = 0; i < 100; i++) {
            listView.items().add("Hello World");
        }

        flowPane.getChildren().add(listView);

        Scene scene = new Scene(flowPane);

        primaryStage.setSize(854, 480);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Hello World");
        primaryStage.show();
    }
}