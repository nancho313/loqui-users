package com.nancho313.loqui.users;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = "com.nancho313.loqui.users")
public class UsersApplication {
  
  public static void main(String[] args) {
    SpringApplication.run(UsersApplication.class, args);
  }
  
  @PostConstruct
  public void init() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }
  
}
