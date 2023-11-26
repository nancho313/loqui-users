package com.nancho313.loqui.users.test.domain.vo;

import com.nancho313.loqui.users.domain.vo.ContactRequestId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContactRequestIdTest {
  
  @Test
  void buildObjectOk() {
    
    // Arrange
    var id = UUID.randomUUID().toString();
    
    // Act
    var result = new ContactRequestId(id);
    
    // Assert
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(id);
  }
  
  @Test
  void buildObjectOkUsingFactoryMethod() {
    
    // Arrange
    var id = UUID.randomUUID().toString();
    
    // Act
    var result = ContactRequestId.of(id);
    
    // Assert
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(id);
  }
  
  @MethodSource("getInvalidData")
  @ParameterizedTest
  void buildObjectWithInvalidData(String id, String expectedErrorMessage) {
    
    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> new ContactRequestId(id));
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
