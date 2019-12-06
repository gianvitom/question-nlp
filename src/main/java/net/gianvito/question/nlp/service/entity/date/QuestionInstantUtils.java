package net.gianvito.question.nlp.service.entity.date;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class QuestionInstantUtils {

  private static final Clock clock = Clock.systemUTC();

  private QuestionInstantUtils() { // NOSONAR
    // Cannot be constructed alone
  }

  public static Instant now() {
    return truncate(Instant.now(clock));
  }

  public static Instant truncate(Instant instant) {
    return instant.truncatedTo(ChronoUnit.MILLIS);
  }

  public static OffsetDateTime truncate(OffsetDateTime odt) {
    return odt.truncatedTo(ChronoUnit.MILLIS);
  }

  public static OffsetDateTime nowOffsetDateTime() {
    return now().atOffset(ZoneOffset.UTC);
  }

  public static Optional<Instant> ofEpochMilliString(final String epochMillis) {
    try {
      return Optional.of(truncate(Instant.ofEpochMilli(Long.parseLong(epochMillis))));
    } catch (NumberFormatException | DateTimeParseException dtpe) {
      return Optional.empty();
    }
  }
}
