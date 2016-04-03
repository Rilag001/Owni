package se.rickylagerkvist.owni.model;

/**
 * Created by Ricky on 2016-04-03.
 */
public class PeopleCard {

    public String name;
    public int numberOfItems;

    public PeopleCard() {
    }

    public PeopleCard(String name, int numberOfItems) {
        this.name = name;
        this.numberOfItems = numberOfItems;
    }

    // getters
    public String getName() {
        return name;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }


    // setters
    public void setName(String name) {
        this.name = name;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }
}
