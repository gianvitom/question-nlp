package net.gianvito.question.nlp.service;

import net.gianvito.question.nlp.service.entity.QuestionEntity;
import net.gianvito.question.nlp.service.noun.QuestionNoun;
import net.gianvito.question.nlp.service.entity.date.Range;
import java.util.List;

public class QueryResponse {

  private String query;
  private List<QuestionNoun> nouns;
  private List<Range> dates;
  private List<QuestionEntity> entities;

  static QueryResponse toQueryResponse(String optimizedQuery,
      List<QuestionNoun> nouns,
      List<Range> ranges,
      List<QuestionEntity> questionEntities) {
    QueryResponse queryResponse = new QueryResponse();
    queryResponse
        .setQuery(optimizedQuery);
    queryResponse.setNouns(nouns);
    queryResponse.setDates(ranges);
    queryResponse.setEntities(questionEntities);
    return queryResponse;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public List<Range> getDates() {
    return dates;
  }

  public void setDates(List<Range> dates) {
    this.dates = dates;
  }

  public List<QuestionEntity> getEntities() {
    return entities;
  }

  public void setEntities(List<QuestionEntity> entities) {
    this.entities = entities;
  }

  public List<QuestionNoun> getNouns() {
    return nouns;
  }

  public void setNouns(List<QuestionNoun> nouns) {
    this.nouns = nouns;
  }
}
