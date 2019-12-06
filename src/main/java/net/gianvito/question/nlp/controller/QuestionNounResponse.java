package net.gianvito.question.nlp.controller;

import net.gianvito.question.nlp.service.noun.QuestionNoun;
import net.gianvito.question.nlp.service.grammar.QuestionGrammarUtils;
import edu.stanford.nlp.ling.IndexedWord;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionNounResponse {

  private String word;
  private List<String> adjectives;
  private List<Connection> connections;

  public static QuestionNounResponse toQuestionNounResponse(QuestionNoun questionNoun) {
    QuestionNounResponse questionNounResponse = new QuestionNounResponse();
    questionNounResponse.word = questionNoun.word().word();
    questionNounResponse.adjectives = questionNoun.adjectives().stream().map(IndexedWord::word)
        .collect(Collectors.toList());

    questionNounResponse.connections = questionNoun.connections().stream().map(
        e -> new QuestionNounResponse.Connection(
            QuestionGrammarUtils.toFullExpression(e.getTarget()),
            toConnectionTypeForResponse(e)))
        .collect(Collectors.toList());

    return questionNounResponse;
  }

  private static String toConnectionTypeForResponse(QuestionNoun.Connection e) {
    return e.getConjunction().getShortName() + ":" + e.getConjunction().getSpecific();
  }

  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public List<String> getAdjectives() {
    return adjectives;
  }

  public void setAdjectives(List<String> adjectives) {
    this.adjectives = adjectives;
  }

  public List<Connection> getConnections() {
    return connections;
  }

  public void setConnections(
      List<Connection> connections) {
    this.connections = connections;
  }

  public static class Connection {

    private String word;
    private String type;

    public Connection() {
    }

    public Connection(String word, String type) {
      this.word = word;
      this.type = type;
    }

    public String getWord() {
      return word;
    }

    public void setWord(String word) {
      this.word = word;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }
}
