package dev.pasmimmo.servicebus.configuration;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@Slf4j
public class ServiceBusClientBuilderServices {

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
  public ServiceBusClientBuilder initServiceBusClientBuilder(
      ServiceBusProperties serviceBusProperties) {
    log.debug("Instantiating ServiceBus Common Client");
    return new ServiceBusClientBuilder().connectionString(
        serviceBusProperties.getConnectionString());
  }

}
