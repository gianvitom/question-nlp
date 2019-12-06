# Question NLP search

This is a POC regarding a potential use of Stanford CoreNLP as main framework to provide "Question Search"

Since now it's based on english Part of Speech and since in other languages these PoS are almost completely different, it supports only **english** as a language.

Some of the features like date parsing anyway work only in english because they're not supported by Stanford CoreNLP at the moment (and from the most of the frameworks).

## Local testing

### Stanford CoreNLP server

Steps to install:
- Download it from here: https://stanfordnlp.github.io/CoreNLP/
- Unzip it and run it with:
```
java -mx8g -cp "*" edu.stanford.nlp.pipeline.StanfordCoreNLPServer -port 9000 -timeout 15000 -annotators "tokenize,ssplit,pos,ner,depparse"
```

### Service

The port of the service itself when running is: **12345**

To use the **API** we should just call the endpoint:

**_optimize_query?query=question**


## Examples with generated query, dates and entities:

### Normal AND question

question: **What do we know about fresh coffee and red apples last 2 weeks?**


  ```
  "query" : "fresh coffee AND red apples",
  "dates": [
    {
      "field": "published",
      "from": "2019-10-08T00:00:00Z",
      "to": null
    }
  ]
  ```

### AND question with date reference

question: **What do we know about fresh coffee and dirty red apples previous month?**

```
  "query" : "fresh coffee AND dirty red apples",
  "dates": [
    {
      "field": "published",
      "from": "2019-09-01T00:00:00Z",
      "to": "2019-09-30T00:00:00Z"
    }
  ]
```

### OR question with date reference

question: **What do we know about fresh coffee or dirty red apples last 3 days?**


```
  "query" : "fresh coffee OR dirty red apples",
  "dates": [
    {
      "field": "published",
      "from": "2019-10-19T00:00:00Z",
      "to": null
    }
  ]
```

### OR and AND question with date reference with year

question: **What do we know about fresh coffee or dirty red apples and nice houses in 2018?**

```
  "query" : "fresh coffee OR dirty red apples AND nice houses"
  "dates": [
    {
      "field": "published",
      "from": "2018-01-01T00:00:00Z",
      "to": "2018-12-31T00:00:00Z"

    }
  ]
 ```

 ### Entity recognition and date reference

question: **Give me the reports about Nike from the last year**

 ```
  "query" : "reports",
  "dates": [
    {
      "field": "published",
      "from": "2019-01-01T00:00:00Z",
      "to": "2019-12-31T00:00:00Z"
    }
  ],
  "entities": [
    {
      "type": "ORGANIZATION",
      "value": "Nike"
    }
  ]
```

### NOT question

question: **Tell me about green tea but no coffee**

```
 "query" : "green tea AND NOT coffee"
```

### NOT question with negation extracted from the verb

question: **Tell me about green tea but I don't want the coffee**

```
 "query" : "green tea AND NOT coffee"
```

### Company and date reference (months)

question: **Give me the reports about Nike since 6 months ago**

```
 "query" : "reports"
```

dates: it depends on the day the query is generated

### Company and date reference (year)

question: **Reports about Nike in the 2019**

```
  "query" : "Reports",
  "dates": [
    {
      "field": "published",
      "from": "2019-01-01T00:00:00Z",
      "to": "2019-12-31T00:00:00Z"
    }
  ],
  "entities": [
    {
      "type": "ORGANIZATION",
      "value": "Nike"
    }
  ]
```

### AND, AND NOT, entity recognition and date and time reference

question: **What do we know about fresh coffee and dirty red apples and no nice houses for Nike in America On March 1st at 11:30pm?**

```
  "query" : "fresh coffee AND dirty red apples AND NOT nice houses",
  "dates": [
    {
      "field": "published",
      "from": "2020-03-01T23:30:00Z",
      "to": null
    }
  ],
  "entities": [
    {
      "type": "ORGANIZATION",
      "value": "Nike"
    },
    {
      "type": "COUNTRY",
      "value": "America"
    }
  ]
```
