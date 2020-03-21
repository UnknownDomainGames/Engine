package engine.gui.example;

import engine.gui.Scene;
import engine.gui.application.GUIApplication;
import engine.gui.control.Button;
import engine.gui.layout.FlowPane;
import engine.gui.layout.VBox;
import engine.gui.misc.Border;
import engine.gui.misc.HPos;
import engine.gui.misc.Pos;
import engine.gui.stage.Stage;
import engine.gui.text.Text;
import engine.util.Color;

import java.lang.management.ManagementFactory;

public class ExampleGUI extends GUIApplication {
    public static void main(String[] args) {
        System.out.println(ManagementFactory.getRuntimeMXBean().getPid());
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FlowPane flowPane = new FlowPane();
        flowPane.alignment().set(Pos.CENTER);

        VBox vBox = new VBox();
        vBox.alignment().set(HPos.CENTER);
        vBox.spacing().set(5);

        Text text = new Text("Hello World");

        Button sayIt = new Button("Say it");
        sayIt.setBorder(new Border(Color.WHITE));
        sayIt.setOnAction(event -> System.out.println("Hello World"));

        vBox.getChildren().addAll(text, sayIt);

        flowPane.getChildren().add(vBox);

        Scene scene = new Scene(flowPane);

        primaryStage.setSize(854, 480);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Hello World");
        primaryStage.show();
    }
}
