package com.userAuthentication.customExceptions;

import java.time.LocalDateTime;

public class UserAlreadyExistsException extends RuntimeException {

  private String errorCode;
  private LocalDateTime timestamp;

  public UserAlreadyExistsException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
    this.timestamp = LocalDateTime.now();
  }

  public UserAlreadyExistsException(String message) {
    super(message);
  }

  public String getErrorCode() {
    return errorCode;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

}
