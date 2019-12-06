package net.gianvito.question.nlp.service.entity;

import static java.util.stream.Collectors.toList;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.time.TimeAnnotations.TimexAnnotation;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.Pair;
import java.time.Instant;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import net.gianvito.question.nlp.service.entity.date.QuestionDateUtils;
import net.gianvito.question.nlp.service.entity.date.Range;


class EntityTimeHandler {

  private static final Logger LOGGER = Logger.getLogger("EntityTimeHandler");

  private CoreDocument coreDocument;

  public EntityTimeHandler(CoreDocument coreDocument) {
    this.coreDocument = coreDocument;
  }

  public List<Range> buildRanges() {
    if (coreDocument == null || coreDocument.entityMentions().isEmpty()) {
      return Collections.emptyList();
    }

    List<Timex> timexes = coreDocument.entityMentions().stream()
        .filter(e -> QuestionEntityUtils.isDateEntityType(e.entityType()))
        .map(i -> i.coreMap().get(TimexAnnotation.class))
        .filter(timex -> timex != null && ( timex.value() != null || timex.range() != null))
        .filter(distinctByKey(Timex::value))
        .collect(toList());

    Range range = generateRange(timexes);
    return range == null ? Collections.emptyList() : Collections.singletonList(range);
  }

  public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
    Map<Object, Boolean> map = new ConcurrentHashMap<>();
    return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
  }

  private Range generateRange(List<Timex> timexes) {
    if (!timexes.isEmpty()) {
      Timex timex = timexes.get(0);
      Range fromResponse = buildQuestionDateResponseFromSingleTimex(timex);

      // Only 1 timex, can be anything
      if (timexes.size() == 1) {
        return fromResponse;
      }

      // Probably it's a range
      if (timexes.size() == 2) {
        Timex timexTo = timexes.get(1);
        Range toResponse = buildQuestionDateResponseFromSingleTimex(timexTo);
        return fromResponse != null && toResponse != null ?
            new Range(fromResponse.getFrom(), toResponse.getFrom())
            : null;
      }
    }

    return null;
  }

  private Range buildQuestionDateResponseFromSingleTimex(Timex timex) {
    switch (timex.timexType()) {
      case "TIME":
        return generateTimeResponse(timex);
      case "DATE":
        return generateDateResponse(timex);
      case "DURATION":
      default:
        break;
    }

    return null;
  }

  private Range generateDateResponse(Timex timex) {
    try {
      Pair<Calendar, Calendar> dateRange = timex.getRange();
      String fromRange = QuestionDateUtils.format(dateRange.first().getTime());
      String toRange = QuestionDateUtils.format(dateRange.second().getTime());
      Instant from = Instant.parse(fromRange);
      Instant to = fromRange.equals(toRange) ? null : Instant.parse(toRange);
      return new Range(from, to);
    } catch (Exception e) {
      LOGGER.warning("It's not possible to convert this timex " + timex.toString());
      return null;
    }
  }

  private Range generateTimeResponse(Timex timex) {
    return new Range(QuestionDateUtils.parseTimex(timex.value()), null);
  }
}
