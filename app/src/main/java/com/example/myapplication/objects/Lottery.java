package com.example.myapplication.objects;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Random;


/**
 * The Lottery class provides methods to manage a list of participants for a lottery draw.
 * It allows for selecting a random participant and removing them from the list.
 */
public class Lottery {
    private ArrayList<String> lotteryList;


    /**
     * Constructor to initialize the Lottery with a list of participants.
     * @param lotteryList an ArrayList containing the participants' identifiers (e.g., names or IDs).
     */
    public Lottery(ArrayList<String> lotteryList) {
        this.lotteryList = lotteryList;
    }


    /**
     * Removes an item from the lottery list at the specified index.
     * @param idx the index of the item to remove.
     */
    public void removeItem(int idx) {
        this.lotteryList.remove(idx);
    }


    /**
     * Selects a random participant from the lottery list, removes them from the list,
     * and returns their identifier.
     * @return the identifier of the selected participant.
     * @throws IllegalStateException if the lottery list is empty or not initialized.
     */
    public String lotterySelect() {
        if (lotteryList == null || lotteryList.isEmpty()) {
            throw new IllegalStateException("The lottery list is empty or not initialized.");
        }
        Random rand = new Random();
        int length = this.lotteryList.size();
        int index = rand.nextInt(length);
        String selected = this.lotteryList.get(index);
        removeItem(index);
        return selected;
    }
}
