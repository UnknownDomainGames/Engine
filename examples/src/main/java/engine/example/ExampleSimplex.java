package engine.example;

import engine.graphics.image.BufferedImage;
import engine.gui.Scene;
import engine.gui.application.GUIApplication;
import engine.gui.image.Image;
import engine.gui.image.ImageView;
import engine.gui.layout.FlowPane;
import engine.gui.misc.Pos;
import engine.gui.stage.Stage;
import engine.math.OctaveOpenSimplexNoiseSampler;
import engine.util.Color;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExampleSimplex extends GUIApplication {
    public static void main(String[] args) {
        System.out.println(ManagementFactory.getRuntimeMXBean().getPid());
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FlowPane flowPane = new FlowPane();
        flowPane.alignment().set(Pos.CENTER);

        BufferedImage bufferedImage = new BufferedImage(512);
        var sampler = new OctaveOpenSimplexNoiseSampler(System.nanoTime(), IntStream.range(0, 8).boxed().collect(Collectors.toList()));
        var list = new ArrayList<Double>();
//        var sampler = new OpenSimplexNoiseSampler(System.nanoTime());
        var min = Double.MAX_VALUE;
        var max = Double.MIN_VALUE;
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                var sample = sampler.sample(i / 128.0, j / 128.0);
                list.add(sample);
                if (sample < min) min = sample;
                if (max < sample) max = sample;
//                System.out.println(sample);
//                bufferedImage.setPixel(i, j, Color.fromGray((float) sample));
            }
        }
        var list1 = new ArrayList<Double>();
        for (Double d : list) {
            list1.add((d - min) / (max - min));
        }
        for (int i = 0; i < list1.size(); i++) {
            bufferedImage.setPixel(i / bufferedImage.getHeight(), i % bufferedImage.getHeight(), Color.fromGray((float) list1.get(i).doubleValue()));
        }

        ImageView view = new ImageView(new Image(bufferedImage, false));
        view.getCustomWidth().set(256);
        view.getCustomHeight().set(256);

        flowPane.getChildren().add(view);

        Scene scene = new Scene(flowPane);

        primaryStage.setSize(854, 480);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Hello World");
        primaryStage.show();
    }
}
