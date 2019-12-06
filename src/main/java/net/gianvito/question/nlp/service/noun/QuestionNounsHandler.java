package net.gianvito.question.nlp.service.noun;

import static java.util.stream.Collectors.toList;

import net.gianvito.question.nlp.service.noun.QuestionNoun.Connection;
import net.gianvito.question.nlp.service.entity.QuestionEntityUtils;
import net.gianvito.question.nlp.service.grammar.QuestionGrammarUtils;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class QuestionNounsHandler {

  private final CoreDocument coreDocument;

  public QuestionNounsHandler(CoreDocument coreDocument) {
    this.coreDocument = coreDocument;
  }

  public List<QuestionNoun> buildQuestionNouns() {
    if (coreDocument == null || coreDocument.sentences().isEmpty() || (
        coreDocument.sentences().size() > 1)) {
      throw new IllegalArgumentException("coreDocument must contain 1 sentence to parse");
    }

    SemanticGraph semanticGraph = coreDocument.sentences().iterator().next().coreMap()
        .get(EnhancedPlusPlusDependenciesAnnotation.class);

    List<QuestionNoun> nouns = generateQuestionNouns(semanticGraph);

    nouns.forEach(noun -> {
          noun.adjectives(retrieveAdjectives(semanticGraph, noun.word()))
              .connections(retrieveConnections(semanticGraph, noun.word()))
              .negation(retrieveNegation(semanticGraph, noun.word()));
        }
    );

    return nouns;
  }

  private GrammaticalRelation retrieveNegation(SemanticGraph semanticGraph,
      IndexedWord word) {
    Optional<SemanticGraphEdge> negations = semanticGraph.outgoingEdgeList(word).stream()
        .filter(
            i -> QuestionGrammarUtils.NEGATION_MODIFIER_RELATION.getShortName()
                .equals(i.getRelation().getShortName())).findFirst();

    if (negations.isPresent()) {
      return negations.get().getRelation();
    }

    Optional<IndexedWord> verb = semanticGraph.incomingEdgeList(word).stream()
        .filter(
            i -> i.getSource().backingLabel().get(PartOfSpeechAnnotation.class).equals(
                QuestionGrammarUtils.VB))
        .map(SemanticGraphEdge::getSource).findFirst();

    if (verb.isPresent()) {
      Optional<SemanticGraphEdge> verbNegation = semanticGraph.outgoingEdgeList(verb.get()).stream()
          .filter(
              i -> QuestionGrammarUtils.NEGATION_MODIFIER_RELATION.getShortName()
                  .equals(i.getRelation().getShortName()))
          .findFirst();
      if (verbNegation.isPresent()) {
        return verbNegation.get().getRelation();
      }
    }

    return null;
  }

  private List<Connection> retrieveConnections(SemanticGraph semanticGraph,
      IndexedWord word) {
    List<Connection> outgoingConnections = semanticGraph.outgoingEdgeList(word).stream()
        .filter(
            i -> QuestionGrammarUtils.NOUN_CONJUNCTIONS.stream()
                .anyMatch(e -> e.getShortName().equals(i.getRelation().getShortName())) && !i
                .isExtra())
        .map(toQuestionNounConnection(semanticGraph))
        .collect(toList());
    List<Connection> connections = new ArrayList<>(outgoingConnections);

    List<Connection> otherConnections = outgoingConnections.stream().map(
        connectionWord -> retrieveConnections(semanticGraph, connectionWord.getTarget().word()))
        .flatMap(Collection::stream)
        .collect(toList());
    connections.addAll(otherConnections);

    return connections;
  }

  private Function<SemanticGraphEdge, Connection> toQuestionNounConnection(
      SemanticGraph semanticGraph) {
    return e -> new Connection(e.getRelation(),
        new QuestionNoun()
            .word(e.getTarget())
            .negation(retrieveNegation(semanticGraph, e.getTarget()))
            .connections(retrieveConnections(semanticGraph, e.getTarget()))
            .adjectives(retrieveAdjectives(semanticGraph, e.getTarget())));
  }

  private List<IndexedWord> retrieveAdjectives(SemanticGraph semanticGraph,
      IndexedWord word) {
    return semanticGraph.outgoingEdgeList(word).stream()
        .filter(
            i -> i.getRelation().equals(QuestionGrammarUtils.ADJECTIVAL_MODIFIER_RELATION) || i
                .getRelation()
                .equals(QuestionGrammarUtils.NAME_MODIFIER_RELATION)).map(SemanticGraphEdge::getTarget)
        .collect(toList());
  }

  private List<QuestionNoun> generateQuestionNouns(SemanticGraph semanticGraph) {
    return semanticGraph
        .getAllNodesByPartOfSpeechPattern(QuestionGrammarUtils.NOUN_PATTERNS).stream()
        .filter(e -> !QuestionEntityUtils.isDateEntityType(
            e.backingLabel().get(NamedEntityTagAnnotation.class)))
        .map(noun -> new QuestionNoun().word(noun)).collect(
            toList());
  }
}
