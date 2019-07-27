package nullengine.util;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.ZonedDateTime;

public class LoggerUtils {

    public static void initLogger(Path loggingPath, boolean debug) {
        initLogger(loggingPath.resolve("Log_" + ZonedDateTime.now().format(TimeUtils.FILE_SAFE_DATE_TIME) + ".log").toAbsolutePath().toString(), debug);
    }

    public static void initLogger(String loggingFileName, boolean debug) {
        var rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        var context = rootLogger.getLoggerContext();

        var pattern = "[%d{HH:mm:ss}] [%logger/%thread] [%level]: %msg%n";
        var level = debug ? Level.DEBUG : Level.INFO;

        var patternLayoutEncoder = new PatternLayoutEncoder();
        patternLayoutEncoder.setCharset(StandardCharsets.UTF_8);
        patternLayoutEncoder.setContext(context);
        patternLayoutEncoder.setPattern(pattern);
        patternLayoutEncoder.start();

        var consoleAppender = new ConsoleAppender<ILoggingEvent>();
        consoleAppender.setContext(context);
        consoleAppender.setName("Console");
        consoleAppender.setEncoder(patternLayoutEncoder);
        consoleAppender.start();

        var fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setFile(loggingFileName);
        fileAppender.setName("File");
        fileAppender.setEncoder(patternLayoutEncoder);
        fileAppender.setContext(context);
        fileAppender.start();

        var asyncAppender = new AsyncAppender();
        asyncAppender.setContext(context);
        asyncAppender.setName("Async");
        asyncAppender.addAppender(consoleAppender);
        asyncAppender.addAppender(fileAppender);
        asyncAppender.start();
        rootLogger.addAppender(asyncAppender);

        rootLogger.setLevel(level);
    }

    public static void setLevel(Level level) {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(level);
    }
}
