package dev.pasmimmo.servicebus.DeadLetterClient;

public interface DeadLetterClient {

  void startDLQRecover();

  void stopDLQRecover();

  boolean isActive();

  String getClientName();

}
