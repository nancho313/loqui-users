package com.nancho313.loqui.users.test.domain.entity;

import com.nancho313.loqui.users.domain.entity.Contact;
import com.nancho313.loqui.users.domain.vo.ContactStatus;
import com.nancho313.loqui.users.domain.vo.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContactTest {
  
  @Test
  void buildObjectOk() {
    
    // Arrange
    var id = UserId.of(UUID.randomUUID().toString());
    var status = ContactStatus.AVAILABLE;
    
    // Act
    var result = new Contact(id, status);
    
    // Assert
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(id);
    assertThat(result.status()).isEqualTo(status);
  }
  
  @MethodSource("getInvalidData")
  @ParameterizedTest
  void buildObjectWithInvalidData(UserId id, ContactStatus status, String expectedErrorMessage) {
    
    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> new Contact(id, status));
    assertThat(exception.getMessage()).contains(expectedErrorMessage);
  }
  
  public static Stream<Arguments> getInvalidData() {
    
    var id = UserId.of(UUID.randomUUID().toString());
    var status = ContactStatus.AVAILABLE;
    
    var expectedErrorMessage1 = "The id cannot be null.";
    var expectedErrorMessage2 = "The status cannot be null.";
    
    return Stream.of(
            Arguments.of(null, status, expectedErrorMessage1),
            Arguments.of(id, null, expectedErrorMessage2)
    );
  }
}
