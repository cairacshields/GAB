package com.example.imac.gab;

/**
 * Created by imac on 4/20/17.
 */

public class group {

    public String groupName;
    public String groupCreator;
    public String groupCreationDate;
    public String groupId;
    public String description;

    public group(){

    }
   public group(String description,String groupName, String groupCreator, String groupCreationDate, String groupId){

       this.groupName = groupName;
       this.groupCreator= groupCreator;
       this.groupCreationDate = groupCreationDate;
       this.groupId = groupId;
       this.description = description;
   }
}
