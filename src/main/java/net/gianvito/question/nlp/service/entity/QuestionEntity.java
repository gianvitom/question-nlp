package net.gianvito.question.nlp.service.entity;

import edu.stanford.nlp.pipeline.CoreEntityMention;

public class QuestionEntity {

  private CoreEntityMention entity;

  public QuestionEntity(CoreEntityMention entity) {
    this.entity = entity;
  }

  public CoreEntityMention getEntity() {
    return entity;
  }

  public void setEntity(CoreEntityMention entity) {
    this.entity = entity;
  }
}
