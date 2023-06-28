package dev.pasmimmo.cosmos.configuration;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@Slf4j
public class CosmosAsyncClientService {

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)

  public CosmosAsyncClient initCosmosClient(CosmosProperties properties) {
    return new CosmosClientBuilder().directMode().endpoint(properties.getEndpoint())
        .key(properties.getKey())
        .contentResponseOnWriteEnabled(properties.isContentResponseOnWriteEnabled())
        .connectionSharingAcrossClientsEnabled(properties.isSharedConnection()).buildAsyncClient();
  }

}
