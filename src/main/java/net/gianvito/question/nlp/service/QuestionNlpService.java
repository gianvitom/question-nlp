package net.gianvito.question.nlp.service;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import net.gianvito.question.nlp.config.StanfordConfig;
import net.gianvito.question.nlp.service.entity.QuestionEntityHandler;
import net.gianvito.question.nlp.service.entity.QuestionEntity;
import net.gianvito.question.nlp.service.grammar.QuestionGrammar;
import net.gianvito.question.nlp.service.noun.QuestionNounsHandler;
import net.gianvito.question.nlp.service.noun.QuestionNoun;
import net.gianvito.question.nlp.service.entity.date.Range;
import net.gianvito.question.nlp.service.grammar.QuestionGrammarUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLPClient;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class QuestionNlpService {

  private static final boolean ONLY_NOUNS_GRAMMAR = false;
  private static final String ANNOTATORS = "tokenize,ssplit,pos,lemma,ner,depparse";

  private StanfordCoreNLPClient nlpClient;

  public QuestionNlpService(StanfordConfig config) {
    Properties tokenizerProps = new Properties();

    tokenizerProps.setProperty("annotators", ANNOTATORS);
    tokenizerProps.setProperty("ner.docdate.usePresent", "true");
    tokenizerProps.setProperty("ner.sutime.includeRange", "true");
    tokenizerProps.setProperty("ner.sutime.markTimeRanges", "true");
    tokenizerProps.setProperty("ner.sutime.teRelHeurLevel", "MORE");

    this.nlpClient = new StanfordCoreNLPClient(tokenizerProps, config.getUrl(), config.getPort());
  }

  private static Function<QuestionGrammar, String> generateGrammarExpressionOrSubExpression() {
    return e -> e.getExpressionsCount() > 1 ? QuestionGrammarUtils.toSubExpressions(e.getText())
        : e.getText();
  }

  public QueryResponse generateOptimizedQuery(final String query) {
    // TODO: Recognise it's a question.

    Annotation document = new Annotation(query);
    Instant now = Instant.now();
    document.set(CoreAnnotations.DocDateAnnotation.class, now.toString());
    nlpClient.annotate(document);
    CoreDocument coreDocument = new CoreDocument(document);

    QuestionEntityHandler questionEntityHandler = new QuestionEntityHandler(coreDocument);
    QuestionNounsHandler questionNounsHandler = new QuestionNounsHandler(coreDocument);

    List<QuestionNoun> nouns = questionNounsHandler.buildQuestionNouns();
    List<QuestionEntity> questionEntities = questionEntityHandler.buildQuestionEntities();
    List<Range> ranges = questionEntityHandler.buildQuestionRanges();

    String optimizedQuery = generateGrammar(nouns, questionEntities);

    return QueryResponse.toQueryResponse(optimizedQuery, nouns, ranges, questionEntities);
  }

  private String generateGrammar(List<QuestionNoun> nouns,
      List<QuestionEntity> questionEntities) {
    QuestionGrammar nounsGrammar = generateNounsGrammar(nouns);

    if (ONLY_NOUNS_GRAMMAR) {
      return nounsGrammar.getText();
    }

    QuestionGrammar entitiesGrammar = generateEntitiesGrammar(questionEntities);

    return generateSubExpressionsGrammars(nounsGrammar, entitiesGrammar);
  }

  private String generateSubExpressionsGrammars(QuestionGrammar... multipleGrammars) {
    List<QuestionGrammar> grammars = Stream.of(multipleGrammars).filter(
        Objects::nonNull).filter(e -> StringUtils.isNotBlank(e.getText())).collect(toList());

    if (grammars.size() > 1) {
      return grammars.stream()
          .map(generateGrammarExpressionOrSubExpression())
          .collect(joining(QuestionGrammarUtils.DEFAULT_CONJUNCTION));
    } else {
      return grammars.stream().filter(Objects::nonNull)
          .map(generateGrammarExpressionOrSubExpression())
          .collect(joining(QuestionGrammarUtils.DEFAULT_CONJUNCTION));
    }
  }

  private QuestionGrammar generateNounsGrammar(List<QuestionNoun> nouns) {
    String grammarText = nouns.stream()
        .filter(e -> isNotInAConnection(nouns, e))
        .map(QuestionGrammarUtils::toFullExpression)
        .collect(joining(QuestionGrammarUtils.DEFAULT_CONJUNCTION));
    return new QuestionGrammar(grammarText, nouns.size());
  }

  private boolean isNotInAConnection(List<QuestionNoun> nouns, QuestionNoun questionNoun) {
    return nouns.stream()
        .noneMatch(noun -> noun.connections().stream()
            .anyMatch(i -> i.getTarget().word().equals(questionNoun.word())));
  }

  private QuestionGrammar generateEntitiesGrammar(List<QuestionEntity> questionEntities) {
    if (CollectionUtils.isEmpty(questionEntities)) {
      return null;
    }

    List<String> expressions = questionEntities.stream()
        .map(e -> QuestionGrammarUtils.toQuotedExpression(e.getEntity().text()))
        .collect(toList());

    String grammarText = String.join(QuestionGrammarUtils.OR_CONJUNCTION, expressions);

    return new QuestionGrammar(grammarText, expressions.size());
  }
}
