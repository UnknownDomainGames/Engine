package engine.graphics.animation;

import com.github.mouse0w0.observable.value.ObservableValueBase;
import com.github.mouse0w0.observable.value.ValueChangeListener;

import java.util.List;

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
        if(done) return;
        if(progress > animation.getDuration()){
            if(looping){
                progress -= animation.getDuration();
            }
        }
        for (Animation child : animation.getChildren()) {
            var property = child.getAnimatingProperty();
            List<ValueChangeListener> l = null;
            if (property instanceof ObservableValueBase) {
                try {
                    var f = ObservableValueBase.class.getDeclaredField("changeListeners");
                    f.setAccessible(true);
                    l = (List<ValueChangeListener>) f.get(property);
                    if (l.size() != 0) {
                        l.forEach(listener -> property.removeChangeListener(listener));
                    }
                } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
            var next = child.getNextFrame(progress);
            if (progress > child.getDuration()) {
                property.setValue(next.getValue());
                continue;
            }
            var pre = child.getPreviousFrame(progress);
            var t = (progress - pre.getTimestamp()) / (next.getTimestamp() - pre.getTimestamp());
            property.setValue(next.getInterpolation().interpolate(pre.getValue(), next.getValue(), t));
            if (l != null) {
                l.forEach(listener -> property.addChangeListener(listener));
            }
        }
        if(progress > animation.getDuration()){
            if(!looping){
                done = true;
            }
        }
    }
}
