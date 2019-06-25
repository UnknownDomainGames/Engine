package nullengine.client.rendering.display;

import javax.annotation.Nonnull;

public interface Cursor {

    @Nonnull
    CursorState getCursorState();

    void setCursorState(@Nonnull CursorState state);

    default boolean isHiddenCursor() {
        return getCursorState() != CursorState.NORMAL;
    }

    default void disableCursor() {
        setCursorState(CursorState.DISABLED);
    }

    default void showCursor() {
        setCursorState(CursorState.NORMAL);
    }

    @Nonnull
    CursorShape getCursorShape();

    void setCursorShape(@Nonnull CursorShape shape);
}
