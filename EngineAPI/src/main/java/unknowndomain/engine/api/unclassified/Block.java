package unknowndomain.engine.api.unclassified;

import javax.annotation.Nonnull;

public interface Block extends FlyweightObject<BlockEntity, World> {

    @Nonnull
    PlaceBehavior placeBehavior();

    @Nonnull
    ActiveBehavior activeBehavior();

    @Nonnull
    TouchBehavior touchBehavior();

    @Nonnull
    DestroyBehavior distroyBahavior();

    // all these behaviors are missing arguments
    // fill those arguments later

    interface PlaceBehavior {
        boolean onPrePlace(BlockEntity block);

        void onPlaced(BlockEntity block);
    }

    interface ActiveBehavior { // right click entity
        boolean onActivate(BlockEntity block);

        void onActivated(BlockEntity block);
    }

    interface TouchBehavior { // left click entity
        boolean onTouch(BlockEntity block);

        void onTouched(BlockEntity block);
    }

    interface DestroyBehavior {

    }
}
