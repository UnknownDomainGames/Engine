package engine.graphics.animation;

import com.github.mouse0w0.observable.value.MutableValue;
import com.github.mouse0w0.observable.value.ObservableValueBase;
import com.github.mouse0w0.observable.value.ValueChangeListener;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyFramePropertyAnimation<T> implements Animation {
    private MutableValue<T> animatingProperty;
    private Map<Double, AnimationKeyframe<T>> keyframes = new HashMap<>();

    public KeyFramePropertyAnimation(MutableValue<T> prop) {
        this.animatingProperty = prop;
    }

    @Override
    public double getDuration() {
        return keyframes.keySet().stream().max(Comparator.naturalOrder()).orElse(0.0);
    }

    @Override
    public void animate(double progress, double delta) {
        var property = this.getAnimatingProperty();
        List<ValueChangeListener> l = null;
        if (property instanceof ObservableValueBase) {
            try {
                var f = ObservableValueBase.class.getDeclaredField("changeListeners");
                f.setAccessible(true);
                l = (List<ValueChangeListener>) f.get(property);
                if (l.size() != 0) {
                    l.forEach(property::removeChangeListener);
                }
            } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        var next = this.getNextFrame(progress);
        if (progress > this.getDuration()) {
            property.setValue(next.getValue());
        } else {
            var pre = this.getPreviousFrame(progress);
            var t = (progress - pre.getTimestamp()) / (next.getTimestamp() - pre.getTimestamp());
            property.setValue(next.getInterpolation().interpolate(pre.getValue(), next.getValue(), t));
        }
        if (l != null) {
            l.forEach(property::addChangeListener);
        }
    }

    public MutableValue<T> getAnimatingProperty() {
        return animatingProperty;
    }

    public void addKeyframe(double timestamp, AnimationKeyframe<T> keyframe) {
        keyframes.put(timestamp, keyframe);
        keyframe.setTimestamp(timestamp);
    }

    public AnimationKeyframe<T> getNextFrame(double progress) {
        if (keyframes.size() == 0) return null;
        var doubleStream = keyframes.keySet().parallelStream().sorted(Comparator.naturalOrder()).filter(d -> progress <= d);
        var nextKeyframe = doubleStream.findFirst().orElse(getDuration());
        return keyframes.get(nextKeyframe);
    }

    public AnimationKeyframe<T> getPreviousFrame(double progress) {
        if (keyframes.size() == 0) return null;
        var keyframe = keyframes.keySet().parallelStream().filter(d -> progress > d).max(Comparator.naturalOrder()).orElse(0.0);
        if (keyframe == 0.0 && !keyframes.containsKey(0.0)) { //When time=0 has not set a keyframe, use the first keyframe instead
            return keyframes.entrySet().stream().min(Comparator.comparingDouble(Map.Entry::getKey)).get().getValue();
        }
        return keyframes.get(keyframe);
    }
}
