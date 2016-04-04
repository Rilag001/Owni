package se.rickylagerkvist.owni.model;

/**
 * Created by Ricky on 2016-04-03.
 */
public class ActivityCard {

    public String nameOfActivity;
    public int numberOfItems;

    public ActivityCard() {
    }

    public ActivityCard(String nameOfActivity, int numberOfItems) {
        this.nameOfActivity = nameOfActivity;
        this.numberOfItems = numberOfItems;
    }

    // getters
    public String getNameOfActivity() {
        return nameOfActivity;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }


    // setters
    public void setNameOfActivity(String nameOfActivity) {
        this.nameOfActivity = nameOfActivity;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }
}
