package it.polito.mobilecourseproject.poliapp.model;

/**
 * Created by nicof on 08/02/2016.
 */
public  class UserInfo{
    public  String userID="";
    public  String name="";
    public UserInfo(String userID,String name){
        this.userID=userID;
        this.name=name;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof UserInfo){
           return ((UserInfo) o).userID.equals(userID) ;
        }
        return false;
    }
}
