package net.gianvito.question.nlp.service.entity.date;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;

/**
 * Used to handle dates.
 */
public class QuestionDateUtils {

  /**
   * Fixed formats
   */
  public static final String ISO_8601_YYYY_MM_DD_HH_MM_SS_SSS_FORMAT =
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  public static final String ISO_8601_YYYY_MM_DD_HH_MM_SS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
  public static final String ISO_8601_YYYY_MM_DD_FORMAT = "yyyy-MM-dd";
  public static final String ISO_8601_YYYY_MM_FORMAT = "yyyy-MM";
  public static final String ISO_8601_YYYY_FORMAT = "yyyy";

  /**
   * Optional formats *
   */
  public static final String ISO_8601_OPTIONAL_MILLIS_DATE_FORMAT =
      "yyyy-MM-dd'T'HH:mm:ss[.SSS][.SS][.S]'Z'";

  public static final String ISO_8601_FORCED_MILLIS_DATE_FORMAT =
      "yyyy-MM-dd'T'HH:mm:ss.[[SSS][SS][S]]'Z'";

  public static final String ISO_8601_OPTIONAL_TIME =
      "yyyy-MM-dd'T'HH:mm[:ss][.SSS][.SS][.S]['Z']";

  private QuestionDateUtils() {
  }

  public static Instant parseTimex(String dateString) {
    return parse(dateString, ISO_8601_OPTIONAL_TIME);
  }

  public static String format(Date date) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
        ISO_8601_YYYY_MM_DD_HH_MM_SS_SSS_FORMAT);
    simpleDateFormat.setTimeZone(TimeZone.getDefault());
    return simpleDateFormat.format(date);
  }

  public static Instant parse(String dateString, String format) {
    if (StringUtils.isEmpty(format) && StringUtils.isEmpty(dateString)) {
      return null;
    }

    if (StringUtils.isNotEmpty(format) && StringUtils.isEmpty(dateString)) {
      throw new IllegalArgumentException(
          "You need to define a value if you provide a format: " + format);
    }

    if (StringUtils.isEmpty(format) && StringUtils.isNotEmpty(dateString)) {
      throw new IllegalArgumentException(
          "You need to define a format if you provide a value: " + dateString);
    }

    try {
      DateTimeFormatter strictFormatter = getDateTimeFormatter(format, ZoneId.of("UTC"));

      return QuestionInstantUtils.truncate(strictFormatter.parse(dateString, Instant::from));

    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException(
          "Date value is not valid for the provided format: " + e.getMessage(), e);
    }
  }

  private static DateTimeFormatter getDateTimeFormatter(String format, ZoneId zoneId) {
    TimeZone.getDefault();

    // @formatter:off
    // @formatter:on
    return new DateTimeFormatterBuilder()
        .appendPattern(format)
        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 00)
        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 00)
        .parseDefaulting(ChronoField.HOUR_OF_DAY, 00)
        .parseDefaulting(ChronoField.DAY_OF_MONTH, 01)
        .parseDefaulting(ChronoField.MONTH_OF_YEAR, 01)
        .parseDefaulting(ChronoField.ERA, 01)
        .toFormatter()
        .withZone(zoneId)
        .withResolverStyle(ResolverStyle.STRICT)
        .withChronology(IsoChronology.INSTANCE);
  }
}
