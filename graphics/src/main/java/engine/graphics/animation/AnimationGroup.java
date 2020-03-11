package engine.graphics.animation;

import java.util.ArrayList;
import java.util.List;

public class AnimationGroup {
    private List<Animation> children = new ArrayList<>();

    public List<Animation> getChildren() {
        return children;
    }

    public double getDuration(){
        return children.stream().mapToDouble(Animation::getDuration).reduce(Double::max).orElse(0);
    }
}
