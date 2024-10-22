package com.example.myapplication;

import java.io.Serializable;

public class Users implements Serializable {
    private String email;


    public Users(String email){
        this.email = email;

    }

    public void setEmail(String uemail){
        this.email = uemail;
    }

    public String getEmail(){
        return this.email;
    }


}
