package com.example.myapplication.objects;

import java.io.Serializable;

public class Users implements Serializable {
    private String device;


    public Users(String email){
        this.device = email;

    }

    public void setEmail(String device){
        this.device = device;
    }

    public String getEmail(){
        return this.device;
    }


}
