package engine.graphics.animation;

public class PlayingAnimation {
    private AnimationGroup animation;
    private double progress;
    private boolean looping;

    private boolean done = false;

    PlayingAnimation(AnimationGroup animation, boolean looping) {
        this.animation = animation;
        this.looping = looping;
    }

    public boolean isDone() {
        return done;
    }

    public void apply(double delta) {
        progress += delta;
        if (done) return;
        if (progress > animation.getDuration()) {
            if (looping) {
                progress -= animation.getDuration();
            }
        }
        for (var child : animation.getChildren()) {
            child.animate(progress, delta);
        }
        if (progress > animation.getDuration()) {
            if (!looping) {
                done = true;
            }
        }
    }
}
