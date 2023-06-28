package dev.pasmimmo.servicebus;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import dev.pasmimmo.servicebus.DeadLetterClient.DeadLetterClient;
import dev.pasmimmo.servicebus.DeadLetterClient.DeadLetterClientImpl;
import java.util.HashMap;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeadLetterController {

  private static BidiMap<String, DeadLetterClient> clientList;
  private final ServiceBusClientBuilder serviceBusClientBuilder;
  private final CosmosAsyncClient asyncClient;

  public DeadLetterController(@Autowired ServiceBusClientBuilder serviceBusClientBuilder,
      @Autowired CosmosAsyncClient asyncClient) {
    this.serviceBusClientBuilder = serviceBusClientBuilder;
    this.asyncClient = asyncClient;
    clientList = new DualHashBidiMap<>();
  }

  public void createClient(String topicName, String subscriptionName) {
    var client = new DeadLetterClientImpl(topicName, subscriptionName, serviceBusClientBuilder,
        asyncClient);
    clientList.put(client.getClientName(), client);
  }

  public void createClient(String queueName) {
    var client = new DeadLetterClientImpl(queueName, serviceBusClientBuilder, asyncClient);
    clientList.put(client.getClientName(), client);
  }

  public void startRecover(String topicName, String subscriptionName) {
    var clientName = topicName + "@" + subscriptionName;
    runRecover(clientName);
  }

  public void startRecover(String queueName) {
    var clientName = queueName + "@";
    runRecover(clientName);
  }

  public void stopRecover(String topicName, String subscriptionName) {
    var clientName = topicName + "@" + subscriptionName;
    stopRun(clientName);
  }

  public void stopRecover(String queueName) throws NoSuchElementException {
    var clientName = queueName + "@";
    stopRun(clientName);
  }

  private void runRecover(String clientName) {
    var client = clientList.get(clientName);
    if (client == null) {
      log.debug("no client found");
      throw new NoSuchElementException("Client not found");
    }
    client.startDLQRecover();
    log.debug("Starting recovery on "+clientName);
  }

  private void stopRun(String clientName) throws NoSuchElementException {
    var client = clientList.get(clientName);
    if (client == null) {
      log.debug("no client found for "+clientName);
      throw new NoSuchElementException("Client not found");
    }
    client.stopDLQRecover();
    clientList.remove(clientName);
    log.debug("Stopping recovery");
  }

  public HashMap<String, String> getClients() {
    var status = new HashMap<String, String>();
    clientList.values().stream().toList().forEach(client -> {
      var key = client.getClientName();
      var value = "is Active: " + client.isActive();
      status.put(key, value);
    });
    return status;
  }

}
