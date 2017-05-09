package com.manywho.services.swagger.exceptions;

public class DataBaseTypeNotSupported extends Exception {
    public DataBaseTypeNotSupported(String typeNotSupported){
        super("database type " + typeNotSupported + " not supported");
    }
}
