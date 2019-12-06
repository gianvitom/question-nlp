package net.gianvito.question.nlp.controller;

import net.gianvito.question.nlp.service.QuestionNlpService;
import net.gianvito.question.nlp.service.QueryResponse;
import net.gianvito.question.nlp.service.grammar.QuestionGrammarUtils;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class QuestionNLPController {

  private final QuestionNlpService questionNlpService;

  public QuestionNLPController(QuestionNlpService questionNlpService) {
    this.questionNlpService = questionNlpService;
  }

  @PostMapping(value = "/_optimize_query")
  public @ResponseBody
  OptimizedQueryResponse optimize(@Valid @RequestBody OptimizedQueryRequest optimizedQueryRequest) {
    String query = optimizedQueryRequest.getQuery();
    if (checkIfQueryShouldNotUseNLP(query)) {
      OptimizedQueryResponse optimizedQueryResponse = new OptimizedQueryResponse();
      optimizedQueryResponse.setQuery(query);
      return optimizedQueryResponse;
    }

    QueryResponse queryResponse = questionNlpService
        .generateOptimizedQuery(query);

    if(StringUtils.isBlank(queryResponse.getQuery())){
      queryResponse.setQuery(query);
    }

    return OptimizedQueryResponse.toOptimizedQueryResponse(queryResponse);
  }

  private boolean checkIfQueryShouldNotUseNLP(String query) {
    return StringUtils.isEmpty(query) || QuestionGrammarUtils.BOOLEANS.stream().anyMatch(query::contains);
  }
}

