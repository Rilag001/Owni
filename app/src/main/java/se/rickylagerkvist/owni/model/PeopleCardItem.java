package se.rickylagerkvist.owni.model;

/**
 * Created by Ricky on 2016-04-03.
 */
public class PeopleCardItem {

    public String description;
    public int amount;
    public String typeOfValue;
    public boolean iOwePersonX;

    public PeopleCardItem() {
    }

    public PeopleCardItem(String description, int amount, String typeOfValue, boolean iOwePersonX) {
        this.description = description;
        this.amount = amount;
        this.typeOfValue = typeOfValue;
        this.iOwePersonX = iOwePersonX;
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

    public boolean isiOwePersonX() {
        return iOwePersonX;
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

    public void setiOwePersonX(boolean iOwePersonX) {
        this.iOwePersonX = iOwePersonX;
    }
}

