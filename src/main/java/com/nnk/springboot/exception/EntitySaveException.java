package com.nnk.springboot.exception;

public class EntitySaveException extends RuntimeException{
    public EntitySaveException (String message){
        super(message);
    }
    public EntitySaveException (String message, Throwable cause){
        super(message, cause);
    }
}
