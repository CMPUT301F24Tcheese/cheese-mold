package com.example.myapplication.objects;

import java.util.ArrayList;
import java.util.Objects;

public class WaitingList extends ArrayList {
    private ArrayList<String> waitingList;

    public WaitingList(ArrayList<String> waitingList){
        this.waitingList = waitingList;
    }

    public ArrayList<String> getList(){
        return this.waitingList;
    }

    public void addUsers(String device){
        this.waitingList.add(device);
    }

    public void removeUsers(String device){
        this.waitingList.remove(device);
    }

    public String getUser(int position){
        return this.waitingList.get(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaitingList that = (WaitingList) o;
        return Objects.equals(waitingList, that.waitingList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(waitingList);
    }

}