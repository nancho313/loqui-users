package com.nancho313.loqui.users.integrationtest.util;

import org.springframework.kafka.annotation.KafkaHandler;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@KafkaHandler(isDefault = true)
public @interface ITKafkaHandler {
}
