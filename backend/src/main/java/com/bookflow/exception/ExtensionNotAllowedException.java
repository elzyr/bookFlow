package com.bookflow.exception;

public class ExtensionNotAllowedException extends  RuntimeException{
   public ExtensionNotAllowedException(String message){
       super(message);
   }
}
