package com.nnk.springboot.exception;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String message){
        super(message);
    }
    public EntityNotFoundException(String message, Throwable cause  ){
        super(message, cause);
    }
    public EntityNotFoundException(String message, int id) {
        super(message + " - ID: " + id);
    }

}

