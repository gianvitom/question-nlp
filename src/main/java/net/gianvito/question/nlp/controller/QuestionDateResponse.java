package net.gianvito.question.nlp.controller;

import java.time.Instant;
import org.apache.commons.lang3.StringUtils;

public class QuestionDateResponse {

  private static final String DEFAULT_DATE_FIELD = "published";
  
  private String field;
  private Instant from;
  private Instant to;

  public QuestionDateResponse(){
  }

  public QuestionDateResponse(String field, Instant from, Instant to) {
    if(StringUtils.isNotBlank(field)){
      this.field = field;
    }else{
      this.field = DEFAULT_DATE_FIELD;
    }

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
