package engine.graphics.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AnimationManager {
    protected Map<String, PlayingAnimation> playingAnimations = new HashMap<>();
    protected List<String> pausedAnimation = new ArrayList<>();
    private boolean globalPause = false;

    public boolean play(String identifier, AnimationGroup animation, boolean isLoop) {
        if (isAnimationPlaying(identifier)) return false;
        var playing = new PlayingAnimation(animation, isLoop);
        playingAnimations.put(identifier, playing);
        return true;
    }

    public boolean isAnimationPlaying(String identifier) {
        return playingAnimations.containsKey(identifier);
    }

    public void stopAnimation(String identifier){
        playingAnimations.remove(identifier);
    }

    public void pauseAnimation(String identifier){
        pausedAnimation.add(identifier);
    }

    public void pauseAll(){
        globalPause = true;
    }

    public void resumeAnimation(String identifier) {
        pausedAnimation.remove(identifier);
    }

    public void resumeAll() {
        globalPause = false;
        pausedAnimation.clear();
    }

    public boolean isPausedGlobally() {
        return globalPause;
    }

    public abstract void update();
}
