package engine.util;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

public interface CrashHandler {

    /**
     * A crash occurs on the current thread
     *
     * @param cause
     * @see Thread#currentThread()
     */
    void crash(@Nonnull Throwable cause);

    /**
     * A crash occurs on a specified thread
     *
     * @param thread
     * @param cause
     */
    void crash(@Nonnull Thread thread, @Nonnull Throwable cause);

    /**
     * A crash occurs on the current thread
     *
     * @param cause
     * @param details
     * @see Thread#currentThread()
     */
    void crash(@Nonnull Throwable cause, @Nonnull Map<String, Consumer<StringBuilder>> details);

    /**
     * A crash occurs on a specified thread
     *
     * @param thread
     * @param cause
     * @param details
     */
    void crash(@Nonnull Thread thread, @Nonnull Throwable cause, @Nonnull Map<String, Consumer<StringBuilder>> details);
}
