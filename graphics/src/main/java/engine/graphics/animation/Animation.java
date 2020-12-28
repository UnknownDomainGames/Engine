package engine.graphics.animation;

public interface Animation {
    double getDuration();

    void animate(double progress, double delta);
}
