package engine.graphics.animation;

public class PlayingAnimation {
    private AnimationGroup animation;
    private double progress;
    private boolean looping;

    private boolean done = false;

    PlayingAnimation(AnimationGroup animation, boolean looping){
        this.animation = animation;
        this.looping = looping;
    }

    public boolean isDone() {
        return done;
    }

    public void apply(double delta) {
        progress += delta;
        if(done) return;
        if(progress > animation.getDuration()){
            if(looping){
                progress -= animation.getDuration();
            }
        }
        for (Animation child : animation.getChildren()) {
            var property = child.getAnimatingProperty();
            var next = child.getNextFrame(progress);
            if(progress > child.getDuration()){
                property.set(next.getValue());
                continue;
            }
            var pre = child.getPreviousFrame(progress);
            var t = (progress - pre.getTimestamp()) / (next.getTimestamp() - pre.getTimestamp());
            property.set(next.getInterpolation().interpolate(pre.getValue(), next.getValue(), t));
        }
        if(progress > animation.getDuration()){
            if(!looping){
                done = true;
            }
        }
    }
}
