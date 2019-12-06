package net.gianvito.question.nlp.controller;

import static java.util.stream.Collectors.toList;

import net.gianvito.question.nlp.service.QueryResponse;
import net.gianvito.question.nlp.service.entity.QuestionEntity;
import net.gianvito.question.nlp.service.noun.QuestionNoun;
import net.gianvito.question.nlp.service.entity.date.Range;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;

public class OptimizedQueryResponse {

  private String query;
  private List<QuestionNounResponse> nouns;
  private List<QuestionDateResponse> dates;
  private List<QuestionEntityResponse> entities;

  public OptimizedQueryResponse() {
  }

  public OptimizedQueryResponse(String query,
      List<QuestionNoun> nouns, List<Range> dates,
      List<QuestionEntity> entities) {
    this.query = query;
    this.nouns = toQuestionNounsResponses(nouns);
    this.dates = toQuestionDateResponses(dates);
    this.entities = toQuestionEntityResponses(entities);
  }

  private static List<QuestionEntityResponse> toQuestionEntityResponses(List<QuestionEntity> entities) {
    return entities.stream().map(QuestionEntity::getEntity)
        .map(e -> new QuestionEntityResponse(e.entityType(), e.text()))
        .collect(toList());
  }

  private static List<QuestionNounResponse> toQuestionNounsResponses(List<QuestionNoun> nouns) {
    if (nouns == null) {
      return new ArrayList<>();
    }

    return nouns.stream().map(QuestionNounResponse::toQuestionNounResponse).collect(Collectors.toList());
  }

  private List<QuestionDateResponse> toQuestionDateResponses(List<Range> dates) {
    if(CollectionUtils.isEmpty(dates)){
      return Collections.emptyList();
    }

    return dates.stream().map(e -> new QuestionDateResponse(e.getField(), e.getFrom(), e.getTo())).collect(
        Collectors.toList());
  }

  public static OptimizedQueryResponse toOptimizedQueryResponse(QueryResponse queryResponse) {
    return new OptimizedQueryResponse(queryResponse.getQuery(), queryResponse.getNouns(),
        queryResponse.getDates(), queryResponse.getEntities());
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public List<QuestionDateResponse> getDates() {
    return dates;
  }

  public void setDates(List<QuestionDateResponse> dates) {
    this.dates = dates;
  }

  public List<QuestionEntityResponse> getEntities() {
    return entities;
  }

  public void setEntities(List<QuestionEntityResponse> entities) {
    this.entities = entities;
  }

  public List<QuestionNounResponse> getNouns() {
    return nouns;
  }

  public void setNouns(List<QuestionNounResponse> nouns) {
    this.nouns = nouns;
  }

}
