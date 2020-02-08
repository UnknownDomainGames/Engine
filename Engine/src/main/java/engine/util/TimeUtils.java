package engine.util;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.*;

public class TimeUtils {

    public static final DateTimeFormatter FILE_SAFE_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public static final DateTimeFormatter DATE_TIME =
            new DateTimeFormatterBuilder()
                    .append(DateTimeFormatter.ISO_LOCAL_DATE)
                    .appendLiteral(" ")
                    .appendValue(HOUR_OF_DAY, 2)
                    .appendLiteral(':')
                    .appendValue(MINUTE_OF_HOUR, 2)
                    .appendLiteral(':')
                    .appendValue(SECOND_OF_MINUTE, 2)
                    .toFormatter();

    public static final DateTimeFormatter OFFSET_DATE_TIME =
            new DateTimeFormatterBuilder()
                    .append(DATE_TIME)
                    .appendLiteral(" ")
                    .appendOffsetId()
                    .toFormatter();
}
