package engine.gui.shape;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import engine.graphics.shape.Path2D;
import engine.gui.graphics.NodeRenderer;

public class Path extends Shape {
    private ObservableList<PathElement> elements = ObservableCollections.observableArrayList();

    private Path2D path2D;

    public ObservableList<PathElement> getPathElements() {
        return elements;
    }

    public void moveTo(float x, float y) {
        elements.clear();
        elements.add(new MoveTo(x, y));
    }

    public void lineTo(float x, float y) {
        elements.add(new LineTo(x, y));
    }

    public void quadTo(float px, float py, float x, float y) {
        elements.add(new QuadTo(px, py, x, y));
    }

    public void curveTo(float px0, float py0, float px1, float py1, float x, float y) {
        elements.add(new CurveTo(px0, py0, px1, py1, x, y));
    }

    public void arcTo(float radiusX, float radiusY, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag, float x, float y) {
        elements.add(new ArcTo(radiusX, radiusY, xAxisRotation, largeArcFlag, sweepFlag, x, y));
    }

    public void closePath() {
        elements.add(new ClosePath());
    }

    @Override
    public float prefWidth() {
        return 0;
    }

    @Override
    public float prefHeight() {
        return 0;
    }

    @Override
    protected NodeRenderer createDefaultRenderer() {
        return null;
    }
}
