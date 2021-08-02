package com.nisum.myteam.statuscodes;

public enum ResourceCodes {

    CREATE(800,"");

    private int code;
    private String message;

   private ResourceCodes(int code,String message){
       this.code=code;
       this.message=message;
   }

   public int getCode()
   {
       return  this.code;
   }

   public String getMessage()
   {
       return  this.message;
   }

}
