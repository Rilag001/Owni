package se.rickylagerkvist.owni.model;

/**
 * Created by Ricky on 2016-04-03.
 */
public class PeopleCardItem {

    public String description;
    public int amount;
    public String typeOfValue;
    public boolean iOwe;


    public PeopleCardItem() {
    }

    public PeopleCardItem(String description, int amount, String typeOfValue, Boolean iOwe) {
        this.description = description;
        this.amount = amount;
        this.typeOfValue = typeOfValue;
        this.iOwe = iOwe;
    }

    // getters
    public String getDescription() {
        return description;
    }

    public int getAmount() {
        return amount;
    }

    public String getTypeOfValue() {
        return typeOfValue;
    }

    public boolean isiOwe() {
        return iOwe;
    }

    // setters
    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setTypeOfValue(String typeOfValue) {
        this.typeOfValue = typeOfValue;
    }

    public void setiOwe(boolean iOwe) {
        this.iOwe = iOwe;
    }
}

