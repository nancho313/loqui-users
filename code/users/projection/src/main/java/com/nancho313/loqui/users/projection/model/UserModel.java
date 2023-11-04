package com.nancho313.loqui.users.projection.model;

import java.util.Objects;

public record UserModel(String id, String username, String email) {
  
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;
    UserModel userModel = (UserModel) object;
    return Objects.equals(id, userModel.id);
  }
  
  public int hashCode() {
    return Objects.hash(id);
  }
}
