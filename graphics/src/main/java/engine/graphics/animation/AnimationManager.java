package engine.graphics.animation;

import java.util.*;

public class AnimationManager {
    private Map<String, PlayingAnimation> playingAnimations = new HashMap<>();
    private List<String> pausedAnimation = new ArrayList<>();

    private double lastTime = Double.NaN;

    public boolean play(String identifier, AnimationGroup animation, boolean isLoop){
        if(isAnimationPlaying(identifier)) return false;
        var playing = new PlayingAnimation(animation, isLoop);
        playingAnimations.put(identifier, playing);
        return true;
    }

    public boolean isAnimationPlaying(String identifier){
        return playingAnimations.containsKey(identifier);
    }

    public void stopAnimation(String identifier){
        playingAnimations.remove(identifier);
    }

    public void pauseAnimation(String identifier){
        pausedAnimation.add(identifier);
    }

    private boolean globalPause = false;

    public void pauseAll(){
        globalPause = true;
    }

    public void resumeAnimation(String identifier){
        pausedAnimation.remove(identifier);
    }

    public void resumeAll(){
        globalPause = false;
        pausedAnimation.clear();
    }

    public void update(){
        var now = System.nanoTime() / 1000000D;
        if (!globalPause && !Double.isNaN(lastTime)) {
            var delta = now - lastTime;
            for (var iterator = playingAnimations.entrySet().iterator(); iterator.hasNext(); ) {
                var next = iterator.next();
                if (pausedAnimation.contains(next.getKey())) continue;
                PlayingAnimation playingAnimation = next.getValue();
                playingAnimation.apply(delta);
                if (playingAnimation.isDone()) iterator.remove();
            }
        }
        lastTime = now;
    }
}
