package org.tui.codechallenge.services.exceptions;

public class DomainNotFoundException extends RuntimeException {

    public DomainNotFoundException(String message) {
        super(message);
    }

    public String getErrorMessage() {
        return String.format("Domain Error - %s", this.getMessage());
    }

}