package se.rickylagerkvist.owni.model;

/**
 * Created by Ricky on 2016-04-03.
 */
public class PeopleCard {
    
    public String name;
    public int numberOfItems;
    public int balance;

    public PeopleCard() {
    }

    public PeopleCard(String name) {
        this.name = name;
        this.numberOfItems = 0;
        this.balance = 0;
    }

    // getters
    public String getName() {
        return name;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public int getBalance() {
        return balance;
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
