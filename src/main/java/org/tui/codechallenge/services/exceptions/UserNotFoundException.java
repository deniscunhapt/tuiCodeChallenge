package org.tui.codechallenge.services.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }

    public String getErrorMessage() {
        return String.format("User Not Found - %s", this.getMessage());
    }
}
