package com.nancho313.loqui.users.test.domain.vo;

import com.nancho313.loqui.users.domain.vo.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserIdTest {
  
  @Test
  void buildObjectOk() {
    
    // Arrange
    var id = UUID.randomUUID().toString();
    
    // Act
    var result = new UserId(id);
    
    // Assert
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(id);
  }
  
  @Test
  void buildObjectUsingFactoryMethodOk() {
    
    // Arrange
    var id = UUID.randomUUID().toString();
    
    // Act
    var result = UserId.of(id);
    
    // Assert
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(id);
  }
  
  @MethodSource("getInvalidData")
  @ParameterizedTest
  void buildObjectWithInvalidData(String id, String expectedErrorMessage) {
    
    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> new UserId(id));
    assertThat(exception.getMessage()).contains(expectedErrorMessage);
  }
  
  public static Stream<Arguments> getInvalidData() {
    
    String nullId = null;
    
    var expectedErrorMessage1 = "The id cannot be empty.";
    
    return Stream.of(
            Arguments.of(nullId, expectedErrorMessage1),
            Arguments.of("", expectedErrorMessage1),
            Arguments.of("  ", expectedErrorMessage1)
    );
  }
}
