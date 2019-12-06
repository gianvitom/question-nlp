package net.gianvito.question.nlp.service.entity.date;

import java.time.Instant;

public class Range {
  private static final String DEFAULT_DATE_FIELD = "published";

  private String field;
  private Instant from;
  private Instant to;

  public Range(Instant from, Instant to) {
    this.field = DEFAULT_DATE_FIELD;
    this.from = from;
    this.to = to;
  }

  public String getField() {
    return field;
  }


  public void setField(String field) {
    this.field = field;
  }

  public Instant getFrom() {
    return from;
  }

  public void setFrom(Instant from) {
    this.from = from;
  }

  public Instant getTo() {
    return to;
  }

  public void setTo(Instant to) {
    this.to = to;
  }
}
