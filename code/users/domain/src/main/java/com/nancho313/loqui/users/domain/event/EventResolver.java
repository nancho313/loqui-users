package com.nancho313.loqui.users.domain.event;

public interface EventResolver <T extends DomainEvent>{
  
  void processEvent(T t);
  
  Class<T> getType();
}
