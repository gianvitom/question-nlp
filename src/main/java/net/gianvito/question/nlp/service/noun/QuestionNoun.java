package net.gianvito.question.nlp.service.noun;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.GrammaticalRelation;
import java.util.ArrayList;
import java.util.List;

public class QuestionNoun {

  private IndexedWord word;
  private GrammaticalRelation negation;
  private List<IndexedWord> adjectives;
  private List<Connection> connections;

  public QuestionNoun() {
    adjectives = new ArrayList<>();
    connections = new ArrayList<>();
  }

  public IndexedWord word() {
    return word;
  }

  public QuestionNoun word(IndexedWord word) {
    this.word = word;
    return this;
  }

  public List<Connection> connections() {
    return connections;
  }

  public QuestionNoun connections(List<Connection> connections) {
    this.connections = connections;
    return this;
  }

  public List<IndexedWord> adjectives() {
    return adjectives;
  }

  public QuestionNoun adjectives(List<IndexedWord> adjectives) {
    this.adjectives = adjectives;
    return this;
  }

  public GrammaticalRelation negation() {
    return negation;
  }

  public QuestionNoun negation(GrammaticalRelation negation) {
    this.negation = negation;
    return this;
  }

  public static class Connection {

    private GrammaticalRelation conjunction;
    private QuestionNoun target;

    public Connection() {
    }

    public Connection(GrammaticalRelation conjunction, QuestionNoun target) {
      this.conjunction = conjunction;
      this.target = target;
    }

    public GrammaticalRelation getConjunction() {
      return conjunction;
    }

    public void setConjunction(GrammaticalRelation conjunction) {
      this.conjunction = conjunction;
    }

    public QuestionNoun getTarget() {
      return target;
    }

    public void setTarget(QuestionNoun target) {
      this.target = target;
    }
  }
}

