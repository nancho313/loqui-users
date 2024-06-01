package com.nancho313.loqui.users.test.externalservice;

import com.nancho313.loqui.users.infrastructure.externalservice.IdGeneratorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdGeneratorTest {

  private IdGeneratorImpl sut;

  @BeforeEach
  void setup() {

    sut = new IdGeneratorImpl();
  }

  @Test
  void generateIdOk() {

    // Act
    var result = sut.generateId();

    // Assert
    assertThat(result).isNotNull().isNotBlank();
  }
}
