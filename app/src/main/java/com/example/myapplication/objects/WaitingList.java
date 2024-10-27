package com.example.myapplication.objects;

import java.util.ArrayList;

public class WaitingList extends ArrayList {
    private ArrayList<String> waitingList;

    public WaitingList(){
        this.waitingList = new ArrayList<>();
    }

    public void addUsers(String device){
        this.waitingList.add(device);
    }

    public void removeUsers(String device){
        this.waitingList.remove(device);
    }

}
