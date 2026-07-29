package com.sistemapdv.backend.exception;

public class ResourceDuplicatedException extends RuntimeException{
    public ResourceDuplicatedException(String message){
        super(message);
    }
}
