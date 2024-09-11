package com.paarsh.admin_paarsh.exceptions;

public class UserAlreadyExitsException extends RuntimeException{

    public UserAlreadyExitsException(){

    }
   public UserAlreadyExitsException(String s){
        super(s);
    }
}
