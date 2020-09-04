package engine.graphics.animation;

public class RealtimeAnimationManager extends AnimationManager {

    private double lastTime = Double.NaN;

    @Override
    public void update() {
        var now = System.nanoTime() / 1000000D;
        if (!isPausedGlobally() && !Double.isNaN(lastTime)) {
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
