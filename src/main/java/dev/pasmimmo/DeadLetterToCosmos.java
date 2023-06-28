package dev.pasmimmo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DeadLetterToCosmos {

  public static void main(String[] args) {
    localDevelopment(); // Remove this line when on Cloud Cosmos Instance
    SpringApplication.run(DeadLetterToCosmos.class, args);
  }

  private static void localDevelopment() {
    // Cosmos SSL Skip certificate for Cosmos DB Emulator
    System.setProperty("javax.net.ssl.trustStore", "NUL");
    System.setProperty("javax.net.ssl.trustStoreType", "Windows-ROOT");
  }

}
