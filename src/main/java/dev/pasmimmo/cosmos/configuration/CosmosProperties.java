package dev.pasmimmo.cosmos.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cosmos")
@Data
public class CosmosProperties {

  private String endpoint;
  private String key;
  private boolean sharedConnection;
  private boolean contentResponseOnWriteEnabled;

}
