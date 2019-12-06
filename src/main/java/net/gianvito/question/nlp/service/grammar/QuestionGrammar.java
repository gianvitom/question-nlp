package net.gianvito.question.nlp.service.grammar;

public class QuestionGrammar {

  private String text;
  private int expressionsCount;

  public QuestionGrammar(String text, int expressionsCount) {
    this.text = text;
    this.expressionsCount = expressionsCount;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public int getExpressionsCount() {
    return expressionsCount;
  }

  public void setExpressionsCount(int expressionsCount) {
    this.expressionsCount = expressionsCount;
  }
}
