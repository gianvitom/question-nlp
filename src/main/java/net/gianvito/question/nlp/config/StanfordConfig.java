package net.gianvito.question.nlp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("stanford")
public class StanfordConfig {

  private String url;
  private Integer port;

  public void setUrl(String url) {
    this.url = url;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getUrl() {
    return url;
  }

  public Integer getPort() {
    return port;
  }
  
  @Override
  public String toString() {
    return url + ":" + port;
  }
}
