package nullengine.util;

import nullengine.Engine;
import org.apache.commons.io.output.AppendableOutputStream;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static nullengine.util.TimeUtils.FILE_SAFE_DATE_TIME;
import static nullengine.util.TimeUtils.OFFSET_DATE_TIME;

public class CrashHandlerImpl implements CrashHandler {

    private final Map<String, Consumer<StringBuilder>> details = new LinkedHashMap<>();

    private final Engine engine;
    private final Path crashReportPath;

    public CrashHandlerImpl(Engine engine, Path crashReportPath) {
        this.engine = engine;
        this.crashReportPath = crashReportPath;
    }

    @Override
    public void crash(@Nonnull Throwable cause) {
        crash(cause, Map.of());
    }

    @Override
    public void crash(@Nonnull Thread thread, @Nonnull Throwable cause) {
        crash(thread, cause, Map.of());
    }

    @Override
    public void crash(@Nonnull Throwable cause, @Nonnull Map<String, Consumer<StringBuilder>> details) {
        crash(Thread.currentThread(), cause, details);
    }

    @Override
    public synchronized void crash(@Nonnull Thread thread, @Nonnull Throwable cause, @Nonnull Map<String, Consumer<StringBuilder>> details) {
        engine.getLogger().error("/////////////// CRASH ///////////////");
        engine.getLogger().error("Thread: {}", thread.getName());
        engine.getLogger().error(cause.getMessage(), cause);
        Path crashReportFile = crashReportPath.resolve("Crash_" + now().format(FILE_SAFE_DATE_TIME) + "_" + engine.getSide() + ".txt").toAbsolutePath();
        try {
            if (!Files.exists(crashReportFile.getParent())) {
                Files.createDirectories(crashReportFile.getParent());
            }
            Files.writeString(crashReportFile, generateCrashReport(thread, cause, details));
            engine.getLogger().error("Generated crash report file at {}.", crashReportFile);
        } catch (IOException e) {
            engine.getLogger().warn(format("Cannot generate crash report file at %s.", crashReportFile), e);
        }
        System.exit(1);
    }

    public String generateCrashReport(@Nonnull Thread thread, @Nonnull Throwable cause, @Nonnull Map<String, Consumer<StringBuilder>> details) {
        Objects.requireNonNull(cause);
        Objects.requireNonNull(details);

        var builder = new StringBuilder();

        builder.append("====== Crash Report ======\n");
        builder.append("\n");

        builder.append("Time: ").append(now().format(OFFSET_DATE_TIME)).append("\n");
        builder.append("\n");

        builder.append("--- Exception Stack Traces ---\n");
        builder.append("Thread: ").append(thread.getName()).append("\n");
        try (var output = new AppendableOutputStream<>(builder);
             var print = new PrintStream(output)) {
            cause.printStackTrace(print);
        } catch (IOException ignored) {
        }
        builder.append("-".repeat(64)).append("\n\n");

        builder.append("--- Exception Details ---\n");
        for (var entry : details.entrySet()) {
            builder.append(entry.getKey()).append(": ");
            entry.getValue().accept(builder);
            builder.append("\n");
        }
        builder.append("-".repeat(64)).append("\n\n");

        builder.append("--- System Details ---\n");
        for (var entry : this.details.entrySet()) {
            builder.append(entry.getKey()).append(": ");
            entry.getValue().accept(builder);
            builder.append("\n");
        }
        builder.append("-".repeat(64)).append("\n\n");

        builder.append("--- All Stack Traces ---\n");
        for (var entry : Thread.getAllStackTraces().entrySet()) {
            if (thread.equals(entry.getKey())) continue; // Don't need print the report crash thread stack
            printThreadStack(builder, entry.getKey(), entry.getValue());
            builder.append("\n");
        }
        return builder.toString();
    }

    protected void printThreadStack(StringBuilder builder, Thread thread, StackTraceElement[] stackTrace) {
        builder.append("Thread: ").append(thread.getName()).append("\n");
        builder.append("Daemon: ").append(thread.isDaemon()).append("\n");
        builder.append("StackTrace:\n");
        for (var element : stackTrace) {
            builder.append("\tat ").append(element).append("\n");
        }
    }

    public void addReportDetail(String name, Consumer<StringBuilder> consumer) {
        details.put(name, consumer);
    }
}
