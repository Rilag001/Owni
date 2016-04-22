package se.rickylagerkvist.owni.model;

/**
 * Created by Ricky on 2016-04-03.
 */
public class ActivityCardItem {

    public String description;
    public int amount;
    public String typeOfValue;
    public String nameOfPerson;
    public boolean iOwe;

    public ActivityCardItem() {
    }

    public ActivityCardItem(String description, int amount, String typeOfValue, String nameOfPerson, boolean iOwe) {
        this.description = description;
        this.amount = amount;
        this.typeOfValue = typeOfValue;
        this.nameOfPerson = nameOfPerson;
        this.iOwe = iOwe;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getTypeOfValue() {
        return typeOfValue;
    }

    public void setTypeOfValue(String typeOfValue) {
        this.typeOfValue = typeOfValue;
    }

    public String getNameOfPerson() {
        return nameOfPerson;
    }

    public void setNameOfPerson(String nameOfPerson) {
        this.nameOfPerson = nameOfPerson;
    }

    public boolean isiOwe() {
        return iOwe;
    }

    public void setiOwe(boolean iOwe) {
        this.iOwe = iOwe;
    }
}
