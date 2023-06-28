package dev.pasmimmo.webservice;

import dev.pasmimmo.servicebus.DeadLetterController;
import java.util.HashMap;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/dlq")
public class DeadLetterRestController {


  @Autowired
  DeadLetterController deadLetterController;

  @PostMapping("/{topicName}/{subscriptionName}/start")
  public HttpStatus consumeDlq(@PathVariable String topicName,
      @PathVariable String subscriptionName) {
    deadLetterController.createClient(topicName, subscriptionName);
    deadLetterController.startRecover(topicName, subscriptionName);
    return HttpStatus.ACCEPTED;
  }

  @PostMapping("/{topicName}/{subscriptionName}/stop")
  public HttpStatus stopDlq(@PathVariable String topicName, @PathVariable String subscriptionName) {
    try {
      deadLetterController.stopRecover(topicName, subscriptionName);
      return HttpStatus.OK;
    } catch (NoSuchElementException noSuchElementException) {
      return HttpStatus.NOT_FOUND;
    }
  }

  @PostMapping("/{queueName}/start")
  public HttpStatus consumeDlq(@PathVariable String queueName) {
    deadLetterController.createClient(queueName);
    deadLetterController.startRecover(queueName);
    return HttpStatus.ACCEPTED;
  }

  @PostMapping("/{queueName}/stop")
  public HttpStatus stopDlq(@PathVariable String queueName) {
    try {
      deadLetterController.stopRecover(queueName);
      return HttpStatus.OK;
    } catch (NoSuchElementException noSuchElementException) {
      return HttpStatus.NOT_FOUND;
    }
  }


  @GetMapping("/")
  public HashMap<String, String> getActiveClients() {
    return deadLetterController.getClients();
  }
}
