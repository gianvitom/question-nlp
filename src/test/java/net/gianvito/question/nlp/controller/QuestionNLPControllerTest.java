package net.gianvito.question.nlp.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuestionNLPControllerTest {

  @Autowired private QuestionNLPController questionNLPController;

  private OptimizedQueryResponse getOptimizedQueryResponse(String query) {
    OptimizedQueryRequest optimizedQueryRequest = new OptimizedQueryRequest();
    optimizedQueryRequest.setQuery(query);
    return questionNLPController
        .optimize(optimizedQueryRequest);
  }

  @Test
  public void null_test() {
    try{
      OptimizedQueryResponse optimized = getOptimizedQueryResponse(
          null);
      fail("Expected an exception");
    }catch(Exception e){
      assertEquals("optimize.optimizedQueryRequest.query: must not be null", e.getMessage());
    }
  }

  @Test
  public void empty_test() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "");
    assertEquals("", optimized.getQuery());
    assertNull(optimized.getNouns());
    assertNull(optimized.getDates());
    assertNull(optimized.getEntities());
  }

  @Test
  public void grammar_generation() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "What do we know about fresh coffee and red apples in the last 2 weeks?");
    assertEquals("(( fresh coffee AND red apples ))", optimized.getQuery());
    assertEquals(2, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    // in the last 2 weeks recognized as DURATION. No values
    assertEquals(0, optimized.getDates().size());
  }

  @Test
  public void compound_word() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "how do people shop for multi purpose cleaners?");
    assertEquals("(( people AND (( cleaners OR \"multi purpose cleaners\" )) AND multi AND purpose ))", optimized.getQuery());
    assertEquals(4, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    // in the last 2 weeks recognized as DURATION. No values
    assertEquals(0, optimized.getDates().size());
  }

  @Test
  public void compound_with_no_entities() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "face wash claims");
    assertEquals("(( (( claims OR \"face wash claims\" )) AND face AND wash ))", optimized.getQuery());
    assertEquals(3, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    // in the last 2 weeks recognized as DURATION. No values
    assertEquals(0, optimized.getDates().size());
  }

  @Test
  public void grammar_generation_with_multiple_adjectives() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "What do we know about fresh coffee and dirty red apples last 2 weeks?");
    assertEquals("(( fresh coffee AND dirty red apples ))", optimized.getQuery());
    assertEquals(2, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    assertEquals(1, optimized.getDates().size());
    QuestionDateResponse questionDateResponse = optimized.getDates().get(0);
    assertNotNull(questionDateResponse.getField());
    assertNotNull(questionDateResponse.getFrom());
    assertNull(questionDateResponse.getTo());
  }

  @Test
  public void grammar_generation_with_multiple_adjectives_and_or() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "What do we know about fresh coffee or dirty red apples last 2 weeks?");
    assertEquals("(( fresh coffee OR dirty red apples ))", optimized.getQuery());
    assertEquals(2, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    assertEquals(1, optimized.getDates().size());
    QuestionDateResponse questionDateResponse = optimized.getDates().get(0);
    assertNotNull(questionDateResponse.getField());
    assertNotNull(questionDateResponse.getFrom());
    assertNull(questionDateResponse.getTo());
  }

  @Test
  public void grammar_generation_with_multiple_adjectives_with_or_and() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "What do we know about fresh coffee or dirty red apples and nice houses last 2 weeks?");
    assertEquals("(( fresh coffee OR dirty red apples AND nice houses ))", optimized.getQuery());
    assertEquals(3, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    assertEquals(1, optimized.getDates().size());
    QuestionDateResponse questionDateResponse = optimized.getDates().get(0);
    assertNotNull(questionDateResponse.getField());
    assertNotNull(questionDateResponse.getFrom());
    assertNull(questionDateResponse.getTo());
  }

  @Test
  public void grammar_generation_with_multiple_adjectives_with_and_or() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "What do we know about fresh coffee and dirty red apples or nice houses last 2 weeks?");
    assertEquals("(( fresh coffee AND dirty red apples OR nice houses ))", optimized.getQuery());
    assertEquals(3, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    assertEquals(1, optimized.getDates().size());
    QuestionDateResponse questionDateResponse = optimized.getDates().get(0);
    assertNotNull(questionDateResponse.getField());
    assertNotNull(questionDateResponse.getFrom());
    assertNull(questionDateResponse.getTo());
  }

  @Test
  public void grammar_generation_with_different_question() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "Give me the reports about Nike from the last year");
    assertEquals("reports AND \"Nike\"", optimized.getQuery());
    assertEquals(1, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    assertEquals(1, optimized.getDates().size());
    QuestionDateResponse questionDateResponse = optimized.getDates().get(0);
    assertNotNull(questionDateResponse.getField());
    assertNotNull(questionDateResponse.getFrom());
    assertNotNull(questionDateResponse.getTo());
  }

  @Test
  public void grammar_generation_with_different_question_multiple_years() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "What do we know about fresh coffee for Coca Cola drinks from the last 2 years?");
    assertEquals("(( fresh coffee AND (( drinks OR \"Coca Cola drinks\" )) AND Coca AND Cola )) AND \"Coca Cola\"", optimized.getQuery());
    assertEquals(2, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    // It's recognized as a DURATION so not date fields
    assertEquals(0, optimized.getDates().size());
  }

  @Test
  public void grammar_generation_with_different_question_months() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "Give me the reports about Nike since 6 months ago");
    assertEquals("reports AND \"Nike\"", optimized.getQuery());
    assertEquals(1, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    assertEquals(1, optimized.getDates().size());
    QuestionDateResponse questionDateResponse = optimized.getDates().get(0);
    assertNotNull(questionDateResponse.getField());
    assertNotNull(questionDateResponse.getFrom());
    assertNull(questionDateResponse.getTo());
  }

  @Test
  public void grammar_generation_with_multiple_with_same_noun() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "Tell me about green tea and tea leaves");
    assertEquals("(( green tea AND tea ))", optimized.getQuery());
    assertEquals(2, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    assertEquals(0, optimized.getDates().size());
  }

  @Test
  public void grammar_generation_with_multiple_with_not() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "Tell me about green tea but no tea leaves");
    assertEquals("(( green tea AND NOT tea ))", optimized.getQuery());
    assertEquals(2, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    assertEquals(0, optimized.getDates().size());
  }

  @Test
  public void grammar_generation_with_multiple_with_not_and_adjective() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "Tell me about green tea and no delicious coffee");
    assertEquals("(( green tea AND NOT delicious coffee ))", optimized.getQuery());
    assertEquals(2, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    assertEquals(0, optimized.getDates().size());
  }

  @Test
  public void grammar_generation_with_multiple_with_multiple_not() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "Tell me about green tea but I don't want the coffee");
    assertEquals("(( green tea AND NOT coffee ))", optimized.getQuery());
    assertEquals(2, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    assertEquals(0, optimized.getDates().size());
  }

  @Test
  public void grammar_generation_with_multiple_with_multiple_not_and_after() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "Tell me about green tea but I don't want the coffee and the milk");
    assertEquals("(( green tea AND NOT coffee AND NOT milk ))", optimized.getQuery());
    assertEquals(3, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    assertEquals(0, optimized.getDates().size());
  }

  @Test
  public void grammar_generation_with_multiple_with_multiple_not_with_cardinal_and_time() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "What do we know about fresh coffee and dirty red apples and no nice houses for Nike or Orange in America On March 1st 12:00pm?");
    assertEquals("(( fresh coffee AND dirty red apples AND NOT nice houses )) AND (( \"Nike\" OR \"America\" ))", optimized.getQuery());
    assertEquals(3, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    assertEquals(1, optimized.getDates().size());
    QuestionDateResponse questionDateResponse = optimized.getDates().get(0);
    assertNotNull(questionDateResponse.getField());
    assertNotNull(questionDateResponse.getFrom());
    assertNull(questionDateResponse.getTo());
  }

  @Test
  public void grammar_generation_with_multiple_with_range() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "What do we know about fresh coffee and dirty red apples from March 2018 to June 2019");
    assertEquals("(( fresh coffee AND dirty red apples ))", optimized.getQuery());
    assertEquals(2, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    assertEquals(1, optimized.getDates().size());
    QuestionDateResponse questionDateResponse = optimized.getDates().get(0);
    assertNotNull(questionDateResponse.getField());
    assertEquals("published", questionDateResponse.getField());
    assertNotNull(questionDateResponse.getFrom());
    assertEquals("2018-03-01T00:00:00Z", questionDateResponse.getFrom().toString());
    assertNotNull(questionDateResponse.getTo());
    assertEquals("2019-06-01T00:00:00Z", questionDateResponse.getTo().toString());
  }

  @Test
  public void grammar_generation_with_multiple_with_multiple_not_with_range() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "What do we know about fresh coffee and dirty red apples from 2018 to 2019");
    assertEquals("(( fresh coffee AND dirty red apples ))", optimized.getQuery());
    assertEquals(2, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    assertEquals(1, optimized.getDates().size());
    QuestionDateResponse questionDateResponse = optimized.getDates().get(0);
    assertNotNull(questionDateResponse.getField());
    assertEquals("published", questionDateResponse.getField());
    assertNotNull(questionDateResponse.getFrom());
    assertEquals("2018-01-01T00:00:00Z", questionDateResponse.getFrom().toString());
    assertNotNull(questionDateResponse.getTo());
    assertEquals("2019-01-01T00:00:00Z", questionDateResponse.getTo().toString());
  }

  @Test
  public void grammar_already_contains_AND() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "What do we know about fresh coffee AND dirty red apples from 2018 to 2019");
    assertEquals("What do we know about fresh coffee AND dirty red apples from 2018 to 2019", optimized.getQuery());
    assertNull(optimized.getNouns());
    assertNull(optimized.getDates());
  }

  @Test
  public void grammar_already_contains_OR() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "What do we know about fresh coffee OR dirty red apples from 2018 to 2019");
    assertEquals("What do we know about fresh coffee OR dirty red apples from 2018 to 2019", optimized.getQuery());
    assertNull(optimized.getNouns());
    assertNull(optimized.getDates());
  }

  @Test
  public void grammar_already_contains_NOT() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "What do we know about fresh coffee AND NOT dirty red apples from 2018 to 2019");
    assertEquals("What do we know about fresh coffee AND NOT dirty red apples from 2018 to 2019", optimized.getQuery());
    assertNull(optimized.getNouns());
    assertNull(optimized.getDates());
  }

  @Test
  public void grammar_already_contains_quotes() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "What do we know about \"fresh coffee\" or dirty red apples from 2018 to 2019");
    assertEquals("(( fresh coffee OR dirty red apples ))", optimized.getQuery());
    assertEquals(2, optimized.getNouns().size());
    assertNotNull(optimized.getDates());
    assertEquals(1, optimized.getDates().size());
    QuestionDateResponse questionDateResponse = optimized.getDates().get(0);
    assertNotNull(questionDateResponse.getField());
    assertEquals("published", questionDateResponse.getField());
    assertNotNull(questionDateResponse.getFrom());
    assertEquals("2018-01-01T00:00:00Z", questionDateResponse.getFrom().toString());
    assertNotNull(questionDateResponse.getTo());
    assertEquals("2019-01-01T00:00:00Z", questionDateResponse.getTo().toString());
  }

  @Test
  public void only_entities() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse(
        "What do you know about Aldi for the last year?");
    assertEquals("\"Aldi\"", optimized.getQuery());
    assertEquals(0, optimized.getNouns().size());
    assertEquals(1, optimized.getEntities().size());
    assertEquals(1, optimized.getDates().size());
  }

  @Test
  public void empty_generated_query() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse("Coca-Cola");
    assertEquals("Coca-Cola", optimized.getQuery());
    assertEquals(0, optimized.getNouns().size());
    assertEquals(0, optimized.getEntities().size());
    assertEquals(0, optimized.getDates().size());
  }

  @Test
  public void apple_generated_query() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse("What do we know about iPhones?");
    assertEquals("iPhones", optimized.getQuery());
    assertEquals(1, optimized.getNouns().size());
    assertEquals(0, optimized.getEntities().size());
    assertEquals(0, optimized.getDates().size());
  }

  @Test
  public void coca_cola_generated_query_2_years_back_from_now() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse("What do you know about Coca Cola 2 years back from now?");
    assertEquals("\"Coca Cola\"", optimized.getQuery());
    assertEquals(0, optimized.getNouns().size());
    assertEquals(1, optimized.getEntities().size());
    // Not recognized because it's a duration
    assertEquals(0, optimized.getDates().size());
  }

  @Test
  public void coca_cola_generated_query_connection_with_adjectives() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse("What do you know about new Coca Cola products in the last year?");
    assertEquals("(( new products OR \"Coca Cola products\" )) AND Coca AND Cola AND \"Coca Cola\"", optimized.getQuery());
    assertEquals(1, optimized.getNouns().size());
    assertEquals(1, optimized.getEntities().size());
    assertEquals(1, optimized.getDates().size());
  }

  @Test
  @Ignore("This will be added with the next version of Stanford CoreNLP")
  public void apple_generated_query_with_fiscal_year() {
    OptimizedQueryResponse optimized = getOptimizedQueryResponse("What do we know about iPhones in FYQ4 2019?");
    assertEquals("iPhones", optimized.getQuery());
    assertEquals(1, optimized.getNouns().size());
    assertEquals(0, optimized.getEntities().size());
    assertEquals(0, optimized.getDates().size());
  }
}