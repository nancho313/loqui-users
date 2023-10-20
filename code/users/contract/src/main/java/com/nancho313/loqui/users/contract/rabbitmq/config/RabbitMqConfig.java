package com.nancho313.loqui.users.contract.rabbitmq.config;

import com.nancho313.loqui.users.contract.rabbitmq.listener.RabbitMqListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
  
  private static final String CREATED_USERS_QUEUE = "created-users-queue";
  private static final String ACCEPTED_CONTACT_REQUEST_QUEUE = "accepted-contact-request-loqui-users-queue";
  
  @Bean
  public MessageListenerContainer container(ConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(CREATED_USERS_QUEUE, ACCEPTED_CONTACT_REQUEST_QUEUE);
    container.setMessageListener(listenerAdapter);
    return container;
  }
  
  @Bean
  public MessageListenerAdapter createdUserListenerAdapter(RabbitMqListener listener) {
    
    var messageListenerAdapter = new MessageListenerAdapter(listener);
    messageListenerAdapter.addQueueOrTagToMethodName(CREATED_USERS_QUEUE, "consumeCreatedUserEvent");
    messageListenerAdapter.addQueueOrTagToMethodName(ACCEPTED_CONTACT_REQUEST_QUEUE,
            "consumeAcceptedContactRequestEvent");
    
    return messageListenerAdapter;
  }
}
