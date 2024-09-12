package com.paarsh.admin_paarsh.exceptions;

public class MailIDDoesNotExistsExecption extends RuntimeException{

    public MailIDDoesNotExistsExecption(){

    }
    public MailIDDoesNotExistsExecption(String s){
        super(s);
    }
}
