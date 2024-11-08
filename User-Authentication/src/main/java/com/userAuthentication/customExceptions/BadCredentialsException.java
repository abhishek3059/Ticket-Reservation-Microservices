package com.userAuthentication.customExceptions;

import java.time.LocalDateTime;

public class BadCredentialsException extends RuntimeException {

  private String errorCode;
  private LocalDateTime timestamp;

  public BadCredentialsException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
    this.timestamp = LocalDateTime.now();
  }
  public BadCredentialsException(String message){
    super(message);
  }

  public String getErrorCode() {
    return errorCode;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }
}
