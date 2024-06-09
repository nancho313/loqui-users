package com.nancho313.loqui.users.integrationtest.util;

import org.springframework.messaging.Message;

import java.util.ArrayList;
import java.util.List;

public abstract class KafkaMessageCaptor<M> {

  private List<Message<M>> capturedMessages = new ArrayList<>();

  private void saveMessage(Message<M> message) {

    this.capturedMessages.add(message);
  }

  public void cleanMessages() {

    this.capturedMessages = new ArrayList<>();
  }

  public List<Message<M>> getCapturedMessages() {

    return this.capturedMessages;
  }

  @ITKafkaHandler
  public void captureMessage(Message<M> message) {

    this.saveMessage(message);
  }
}
