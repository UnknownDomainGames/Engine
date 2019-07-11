package nullengine.client.util;

import org.apache.commons.io.output.AppendableOutputStream;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class CrashReporter {

    private final Map<String, Consumer<StringBuilder>> details = new LinkedHashMap<>();

    public void onCrash(@Nonnull Throwable cause) {
        onCrash(cause, Map.of());
    }

    public void onCrash(@Nonnull Throwable cause, @Nonnull Map<String, Consumer<StringBuilder>> details) {

    }

    public String generateCrashReport(@Nonnull Throwable cause) {
        return generateCrashReport(cause, Map.of());
    }

    public String generateCrashReport(@Nonnull Throwable cause, @Nonnull Map<String, Consumer<StringBuilder>> details) {
        Objects.requireNonNull(cause);
        Objects.requireNonNull(details);

        var builder = new StringBuilder();

        builder.append("------ Engine Crash Report ------\n");
        builder.append("\n");

        builder.append("Time: ").append(TimeUtils.getFormattedCurrentTime()).append("\n");
        builder.append("\n");

        builder.append("--- Exception Stack Traces ---\n");
        try (var output = new AppendableOutputStream<>(builder);
             var print = new PrintStream(output)) {
            cause.printStackTrace(print);
        } catch (IOException ignored) {
        }
        builder.append("\n").append("-".repeat(64)).append("\n\n");

        builder.append("--- Exception Details ---\n");
        for (var entry : details.entrySet()) {
            builder.append(entry.getKey()).append(": ");
            entry.getValue().accept(builder);
            builder.append("\n");
        }
        builder.append("\n").append("-".repeat(64)).append("\n\n");

        builder.append("--- System Details ---\n");
        for (var entry : this.details.entrySet()) {
            builder.append(entry.getKey()).append(": ");
            entry.getValue().accept(builder);
            builder.append("\n");
        }
        builder.append("\n").append("-".repeat(64)).append("\n\n");

        builder.append("--- Stack Traces ---\n");
        for (var entry : Thread.getAllStackTraces().entrySet()) {
            printThreadStack(builder, entry.getKey(), entry.getValue());
            builder.append("\n\n");
        }
        return builder.toString();
    }

    protected void printThreadStack(StringBuilder builder, Thread thread, StackTraceElement[] stackTrace) {
        builder.append("Thread: ").append(thread.getName()).append("\n");
        builder.append("StackTrace:\n");
        for (var element : stackTrace) {
            builder.append("\tat ").append(element).append("\n");
        }
    }

    public void addDetail(String name, Consumer<StringBuilder> consumer) {
        details.put(name, consumer);
    }
}
