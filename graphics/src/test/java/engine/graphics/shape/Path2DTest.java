package engine.graphics.shape;

import javax.swing.*;
import java.awt.*;
import java.nio.FloatBuffer;

public class Path2DTest {

    public static void main(String[] args) {
        Path2D path = Path2D.heap();
        path.moveTo(100, 350);
        path.quadTo(250, 50, 400, 350);
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
                for (int i = 0, size = buffer.limit(); i < size; i += 2) {
                    g.fillRect(Math.round(buffer.get(i)), Math.round(buffer.get(i + 1)), 1, 1);
                }
            }
        };
        panel.setSize(500, 500);
        frame.setContentPane(panel);

        frame.setVisible(true);
    }
}
