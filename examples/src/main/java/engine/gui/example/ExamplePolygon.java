package engine.gui.example;

import engine.gui.Scene;
import engine.gui.application.GUIApplication;
import engine.gui.layout.FlowPane;
import engine.gui.misc.Point;
import engine.gui.misc.Pos;
import engine.gui.shape.Polygon;
import engine.gui.stage.Stage;
import engine.util.Color;

public class ExamplePolygon extends GUIApplication {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FlowPane flowPane = new FlowPane();
        flowPane.alignment().set(Pos.CENTER);

        Polygon polygon = new Polygon(new Point(0, 0), new Point(100, 0), new Point(100, 100), new Point(0, 100));
        polygon.setFillColor(Color.WHITE);

        flowPane.getChildren().add(polygon);

        Scene scene = new Scene(flowPane);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
