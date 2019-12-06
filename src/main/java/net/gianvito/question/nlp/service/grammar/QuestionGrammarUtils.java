package net.gianvito.question.nlp.service.grammar;

import net.gianvito.question.nlp.service.noun.QuestionNoun;
import net.gianvito.question.nlp.service.noun.QuestionNoun.Connection;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class QuestionGrammarUtils {

  // Stanford CoreNLP entities
  public static final String NOUN_PATTERNS = "NN|NNS";
  public static final String VB = "VB";
  public static final GrammaticalRelation NEGATION_MODIFIER_RELATION = UniversalEnglishGrammaticalRelations.NEGATION_MODIFIER;
  public static final GrammaticalRelation NAME_MODIFIER_RELATION = UniversalEnglishGrammaticalRelations.NAME_MODIFIER;
  public static final GrammaticalRelation ADJECTIVAL_MODIFIER_RELATION = UniversalEnglishGrammaticalRelations.ADJECTIVAL_MODIFIER;
  private static final String EMPTY_STRING = "";
  // Conjunctions and booleans
  private static final String AND = "AND";
  public static final String AND_CONJUNCTION = " " + AND + " ";
  public static final String DEFAULT_CONJUNCTION = AND_CONJUNCTION;
  private static final String OR = "OR";
  public static final String OR_CONJUNCTION = " " + OR + " ";
  private static final String NOT = "NOT";
  public static final List<String> BOOLEANS = Arrays.asList(AND, OR, NOT);
  public static final String NOT_CONJUNCTION = NOT + " ";
  private static final String DEFAULT_OPERATOR = AND;
  private static final String OPENING_SUB_EXPRESSION = "((";
  private static final String CLOSING_SUB_EXPRESSION = "))";
  private static final String OPENING_SUB_EXPRESSION_CONJUNCTION = OPENING_SUB_EXPRESSION + " ";
  private static final String CLOSING_SUB_EXPRESSION_CONJUNCTION = " " + CLOSING_SUB_EXPRESSION;
  private static final String QUOTED_TERM = "\"";
  private static final GrammaticalRelation COMPOUND_CONJUNCTION = UniversalEnglishGrammaticalRelations.COMPOUND_MODIFIER;
  public static final List<GrammaticalRelation> NOUN_CONJUNCTIONS = Arrays
      .asList(UniversalEnglishGrammaticalRelations.CONJUNCT,
          COMPOUND_CONJUNCTION);

  private QuestionGrammarUtils() {
    // This cannot be instantiated alone
  }

  public static String toSubExpressions(String grammar) {
    if (StringUtils.isBlank(grammar)) {
      return null;
    }

    return OPENING_SUB_EXPRESSION_CONJUNCTION + grammar + CLOSING_SUB_EXPRESSION_CONJUNCTION;
  }

  public static String toQuotedExpression(String entityText) {
    return QUOTED_TERM + entityText + QUOTED_TERM;
  }

  private static String toSingleExpression(QuestionNoun noun) {
    String word = noun.word().originalText();
    String negationGrammar = noun.negation() != null ? NOT_CONJUNCTION : EMPTY_STRING;

    String fullAdjectives = noun.adjectives().stream().map(singleWord -> singleWord.originalText() + singleWord
        .after())
        .collect(Collectors.joining());

    String compoundedQuotedWord = toCompoundedWord(noun, word);
    String singleWord = fullAdjectives + word;

    if(StringUtils.isNotBlank(compoundedQuotedWord) && !singleWord.equals(compoundedQuotedWord) ){
      return negationGrammar + toSubExpressions(singleWord + OR_CONJUNCTION + compoundedQuotedWord);
    }else{
      return negationGrammar + singleWord;
    }
  }

  private static String toCompoundedWord(QuestionNoun noun, String word) {
    if(noun.connections().stream()
        .noneMatch(e -> COMPOUND_CONJUNCTION.getShortName().equals(e.getConjunction().getShortName()))){
      return "";
    }

    String compounded = noun.connections().stream()
        .filter(e -> e.getConjunction().getShortName().equals("compound"))
        .map(e -> e.getTarget().word().originalText())
        .collect(Collectors.joining(" "));

    return toQuotedExpression(compounded + " " + word);
  }

  private static String toBooleanConjunction(Connection e) {
    String conjunction = toBooleanOperator(e);
    if (BOOLEANS.contains(conjunction)) {
      return String.format(" %s ", conjunction);
    }

    return String.format(" %s ", DEFAULT_OPERATOR);
  }

  private static String toBooleanOperator(Connection e) {
    GrammaticalRelation conjunction = e.getConjunction();

    if (COMPOUND_CONJUNCTION.getShortName().equals(conjunction.getShortName())) {
      return DEFAULT_OPERATOR;
    }

    return conjunction.getSpecific().toUpperCase();
  }

  public static String toFullExpression(QuestionNoun noun) {
    String mainWord = toSingleExpression(noun);

    return mainWord + noun.connections().stream()
        .map(e -> toBooleanConjunction(e) + toSingleExpression(e.getTarget()))
        .collect(Collectors.joining());
  }
}
