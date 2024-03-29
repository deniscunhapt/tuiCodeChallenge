package org.tui.codechallenge.services.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }

    public String getErrorMessage() {
        return String.format("%s", this.getMessage());
    }
}
