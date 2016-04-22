package se.rickylagerkvist.owni.model;

/**
 * Created by Ricky on 2016-04-03.
 */
public class ActivityCard {

    public String nameOfActivity;
    public int numberOfItems;
    public int balance;


    public ActivityCard() {
    }

    public ActivityCard(String nameOfActivity) {
        this.nameOfActivity = nameOfActivity;
        this.numberOfItems = 0;
        this.balance = 0;
    }

    // getters
    public String getNameOfActivity() {
        return nameOfActivity;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public int getBalance() {
        return balance;
    }

    // setters
    public void setNameOfActivity(String nameOfActivity) {
        this.nameOfActivity = nameOfActivity;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
