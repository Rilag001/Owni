package se.rickylagerkvist.owni.model;

/**
 * Created by Ricky on 2016-04-03.
 */
public class ActivityCardItem {

    public String nameOfPerson;
    public String description;
    public int amount;
    public String typeOfValue;
    public boolean iOwePersonX;

    public ActivityCardItem() {
    }

    public ActivityCardItem(String nameOfPerson, String description, int amount, String typeOfValue, boolean iOwePersonX) {
        this.nameOfPerson = nameOfPerson;
        this.description = description;
        this.amount = amount;
        this.typeOfValue = typeOfValue;
        this.iOwePersonX = iOwePersonX;
    }

    // getters
    public String getNameOfPerson() {
        return nameOfPerson;
    }

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
    public void setNameOfPerson(String nameOfPerson) {
        this.nameOfPerson = nameOfPerson;
    }

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
