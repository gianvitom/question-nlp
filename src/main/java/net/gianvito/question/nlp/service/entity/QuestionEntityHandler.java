package net.gianvito.question.nlp.service.entity;

import static java.util.stream.Collectors.toList;

import net.gianvito.question.nlp.service.entity.date.Range;
import edu.stanford.nlp.pipeline.CoreDocument;
import java.util.Collections;
import java.util.List;

public class QuestionEntityHandler {

  private CoreDocument coreDocument;
  private EntityTimeHandler entityTimeHandler;

  public QuestionEntityHandler(CoreDocument coreDocument) {
    this.coreDocument = coreDocument;
    this.entityTimeHandler = new EntityTimeHandler(coreDocument);
  }

  public List<QuestionEntity> buildQuestionEntities() {
    if (coreDocument == null || coreDocument.entityMentions().isEmpty()) {
      return Collections.emptyList();
    }

    return coreDocument.entityMentions().stream()
        .filter(e -> !QuestionEntityUtils.isDateEntityType(e.entityType()))
        .map(QuestionEntity::new)
        .collect(toList());
  }

  public List<Range> buildQuestionRanges() {
    return entityTimeHandler.buildRanges();
  }
}
