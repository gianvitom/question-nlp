package net.gianvito.question.nlp.service.entity;

import java.util.Arrays;
import java.util.List;

public class QuestionEntityUtils {

  private QuestionEntityUtils(){
    // This cannot be constructed alone
  }

  private static final List<String> DATE_ENTITY_TYPES = Arrays
      .asList("DATE", "TIME", "DURATION", "SET");

  public static boolean isDateEntityType(String type){
    return QuestionEntityUtils.DATE_ENTITY_TYPES.contains(type);
  }
}
