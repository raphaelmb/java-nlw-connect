package br.com.nlw.events.exception;

public class UserReferrerNotFoundException extends RuntimeException{

    public UserReferrerNotFoundException(String message) {
        super(message);
    }
}
