package org.tui.codechallenge.services.exceptions;

public class ErrorProcessingRepositoryException extends RuntimeException{
    public ErrorProcessingRepositoryException(String message) {
        super(message);
    }

    public String getErrorMessage() {
        return String.format("%s", this.getMessage());
    }
}
