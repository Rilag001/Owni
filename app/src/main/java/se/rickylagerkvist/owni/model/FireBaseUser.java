package se.rickylagerkvist.owni.model;

import java.util.HashMap;

/**
 * Created by Ricky on 2016-04-03.
 */
public class FireBaseUser {

    private String name;
    private String email;
    private HashMap<String, Object> timestampJoined;

    // constructor
    public FireBaseUser() {
    }

    public FireBaseUser(String name, String email, HashMap<String, Object> timestampJoined) {
        this.name = name;
        this.email = email;
        this.timestampJoined = timestampJoined;
    }

    // getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTimestampJoined(HashMap<String, Object> timestampJoined) {
        this.timestampJoined = timestampJoined;
    }
}
