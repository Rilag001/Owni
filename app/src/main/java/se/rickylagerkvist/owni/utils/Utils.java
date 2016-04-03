package se.rickylagerkvist.owni.utils;

/**
 * Created by Ricky on 2016-04-03.
 */
public class Utils {

    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

}
