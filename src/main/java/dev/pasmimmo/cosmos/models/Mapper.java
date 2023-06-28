package dev.pasmimmo.cosmos.models;

import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.context.annotation.Bean;

public class Mapper {

  @Bean
  public ServiceBusJsonBodyMessage mapToCosmos(ServiceBusReceivedMessage message, String cosmosPartitionKey) {

    return ServiceBusJsonBodyMessage.builder()
        .applicationProperties(message.getApplicationProperties())
        .contentType(message.getContentType())
        .correlationId(message.getCorrelationId())
        .deadLetterErrorDescription(message.getDeadLetterErrorDescription())
        .deadLetterReason(message.getDeadLetterReason())
        .deadLetterSource(message.getDeadLetterSource())
        .deliveryCount(message.getDeliveryCount())
        .enqueuedSequenceNumber(message.getEnqueuedSequenceNumber())
        .enqueuedTime(message.getEnqueuedTime())
        .expiresAt(message.getExpiresAt())
        .partitionKey(message.getPartitionKey())
        .replyTo(message.getReplyTo())
        .sessionId(message.getSessionId())
        .subject(message.getSubject())
        .to(message.getTo())
        .messageId(message.getMessageId())
        .body(message.getBody().toObject(JsonNode.class))
        .id(message.getMessageId())
        .partition(cosmosPartitionKey)
        .build();
  }

}
