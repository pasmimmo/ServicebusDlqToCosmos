package dev.pasmimmo.servicebus.DeadLetterClient;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceiverAsyncClient;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import com.azure.messaging.servicebus.models.SubQueue;
import dev.pasmimmo.cosmos.models.Mapper;
import java.time.Duration;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class DeadLetterClientImpl implements DeadLetterClient {

  private final String clientName;
  private final ServiceBusReceiverAsyncClient sbAsyncClient;
  private static final Scheduler dlqScheduler = Schedulers.newParallel("DLQ");
  private final ParallelFlux<ServiceBusReceivedMessage> parallelFlux;
  private Disposable activeFlux;
  private final CosmosAsyncClient cosmosAsyncClient;


  public DeadLetterClientImpl(String topicName, String subscriptionName,
      ServiceBusClientBuilder clientBuilder, CosmosAsyncClient cosmosAsyncClient) {
    this.cosmosAsyncClient = cosmosAsyncClient;

    clientName = topicName + "@" + subscriptionName;

    sbAsyncClient = clientBuilder.receiver() // Use this for session or non-session enabled queue or topic/subscriptions
        .receiveMode(ServiceBusReceiveMode.PEEK_LOCK).topicName(topicName)
        .subscriptionName(subscriptionName).subQueue(SubQueue.DEAD_LETTER_QUEUE)
        .buildAsyncClient();

    parallelFlux = configureFlux();
  }

  public DeadLetterClientImpl(String queueName, ServiceBusClientBuilder clientBuilder,
      CosmosAsyncClient cosmosAsyncClient) {
    this.cosmosAsyncClient = cosmosAsyncClient;
    clientName = queueName + "@";
    sbAsyncClient = clientBuilder.receiver() // Use this for session or non-session enabled queue or topic/subscriptions
        .receiveMode(ServiceBusReceiveMode.PEEK_LOCK).queueName(queueName)
        .subQueue(SubQueue.DEAD_LETTER_QUEUE).buildAsyncClient();

    parallelFlux = configureFlux();
  }

  private ParallelFlux<ServiceBusReceivedMessage> configureFlux() {
    return sbAsyncClient.receiveMessages()
        .parallel()
        .runOn(dlqScheduler)
        .doOnSubscribe(flux -> log.debug("[{}] recover started", this.clientName))
        .doOnError(flux -> log.debug("[{}] error while recovering", this.clientName))
        .doOnNext(message -> log.debug("[{}] received message from dlq with id: {} and reason: {}",
            this.clientName,
            message.getMessageId(),
            message.getDeadLetterReason()));
  }

  private Consumer<? super ServiceBusReceivedMessage> messageToCosmosConsumer() {
    return
        message -> {
          log.debug("Storing Message on DB");
          var mapper = new Mapper();
          var dlqDoc = mapper.mapToCosmos(message, clientName);
          var container = cosmosAsyncClient.getDatabase("dlq-db").getContainer("dlq");
          container.createItem(dlqDoc).timeout(Duration.ofMinutes(2)).subscribe(
              cosmosOpt -> {
                log.debug(cosmosOpt.getDiagnostics().toString());
              }
          );
        };
  }

  @Override
  public void startDLQRecover() {
    if (isActive()) {
      log.debug("Client Recover is already started");
    } else {
      activeFlux = parallelFlux
          .subscribe(messageToCosmosConsumer());
    }

  }

  @Override
  public void stopDLQRecover() {
    if (isActive()) {
      activeFlux.dispose();
      log.debug("Client Recovery disposed");
    } else {
      log.debug("Client Recovery is already disposed");
    }

  }

  @Override
  public boolean isActive() {
    if (activeFlux == null) {
      return false;
    }
    else
      return !activeFlux.isDisposed();
  }

  @Override
  public String getClientName() {
    return clientName;
  }
}
