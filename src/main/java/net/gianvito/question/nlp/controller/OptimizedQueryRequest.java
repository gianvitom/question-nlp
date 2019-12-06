package net.gianvito.question.nlp.controller;

import javax.validation.constraints.NotNull;

public class OptimizedQueryRequest {

  @NotNull
  private String query;

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }
}
