package dev.pasmimmo.cosmos.models;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ServiceBusJsonBodyMessage{

  /* AMQP Fields */
  Map<String, Object> applicationProperties;
  String contentType;
  String correlationId;
  String deadLetterErrorDescription;
  String deadLetterReason;
  String deadLetterSource;
  long deliveryCount;
  long enqueuedSequenceNumber;
  OffsetDateTime enqueuedTime;
  OffsetDateTime expiresAt;
  String partitionKey;
  String replyTo;
  String sessionId;
  String subject;
  String to;
  String messageId;

  JsonNode body;

  /*Cosmos Props*/
  String id;
  String partition;

  /* other prop
  String replyToSessionId;
  String lockToken;
  OffsetDateTime lockedUntil;
  BinaryData body;
   */

}
