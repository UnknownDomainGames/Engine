package engine.graphics.shape;

import javax.swing.*;
import java.awt.*;
import java.nio.FloatBuffer;

public class Path2DTest {

    public static void main(String[] args) {
        Path2D path = Path2D.heap();
        path.moveTo(150, 350);
        path.arcTo(50, 50, 90, false, true, 100, 300);
        path.lineTo(100, 150);
        path.quadTo(250, -50, 400, 150);
        path.lineTo(400, 300);
        path.curveTo(400, 350, 400, 350, 350, 350);
        path.closePath();

        JFrame frame = new JFrame();
        frame.setTitle("Path2DTest");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(Color.BLACK);
                FloatBuffer buffer = path.getBuffer();
                int half = buffer.position() / 2;
                int[] xs = new int[half];
                int[] ys = new int[half];
                for (int i = 0; i < half; i++) {
                    xs[i] = Math.round(buffer.get(i * 2));
                    ys[i] = Math.round(buffer.get(i * 2 + 1));
                }
                g.drawPolygon(xs, ys, half);
            }
        };
        panel.setSize(500, 500);
        frame.setContentPane(panel);

        frame.setVisible(true);
    }
}
