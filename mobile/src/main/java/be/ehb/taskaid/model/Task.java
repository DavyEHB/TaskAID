package be.ehb.taskaid.model;

import java.util.ArrayList;

/**
 * Created by davy.van.belle on 20/08/2015.
 */
public class Task {
    private int ID;
    private String name;
    private String beaconAddress;
    private ArrayList<Card> cards = new ArrayList<>();

    public Task() {
    }

    public Task(int ID, String name, String beaconAddress, ArrayList<Card> cards) {
        this.ID = ID;
        this.name = name;
        this.beaconAddress = beaconAddress;
        this.cards = cards;
    }

    public Task(int ID, String name, String beaconAddress) {
        this.ID = ID;
        this.name = name;
        this.beaconAddress = beaconAddress;
    }

    public Task(String name) {
        this.name = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeaconAddress() {
        return beaconAddress;
    }

    public void setBeaconAddress(String beaconAddress) {
        this.beaconAddress = beaconAddress;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        if (cards != null) {
            this.cards = cards;
            for (Card card : this.cards) {
                card.setTaskID(this.ID);
            }
        }
    }

    public void addCard(Card card){
        if (card != null) {
            this.cards.add(card);
            card.setTaskID(this.ID);
        }
    }

    public void removeCard(Card card){
        if (card != null) {
            this.cards.remove(card);
            card.setTaskID(0);
        }
    }

    @Override
    public String toString() {
        return "ID " + ID + " - " +
                "Name " + name + " - " +
                "Beacon " + beaconAddress + " - " +
                "#cards " + cards.size();
    }
}
