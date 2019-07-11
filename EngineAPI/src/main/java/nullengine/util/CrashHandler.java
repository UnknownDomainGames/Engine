package nullengine.util;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

public interface CrashHandler {
    void crash(@Nonnull Throwable cause);

    void crash(@Nonnull Throwable cause, @Nonnull Map<String, Consumer<StringBuilder>> details);
}
