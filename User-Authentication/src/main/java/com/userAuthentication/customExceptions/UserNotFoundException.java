package com.userAuthentication.customExceptions;

import java.time.LocalDateTime;

public class UserNotFoundException extends RuntimeException {

  private String errorCode;
  private LocalDateTime timestamp;

  public UserNotFoundException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
    this.timestamp = LocalDateTime.now();
  }
  public UserNotFoundException(String message){
    super(message);
  }

  public String getErrorCode() {
    return errorCode;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }
}
