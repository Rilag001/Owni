package se.rickylagerkvist.owni.utils;

/**
 * Created by Ricky on 2016-04-03.
 */
public class Utils {

    // Firebase URL cant contain ".", replace with ","
    public static String replaceDotWithSemiColon(String userEmail) {
        return userEmail.replace(".", ",");
    }

}
