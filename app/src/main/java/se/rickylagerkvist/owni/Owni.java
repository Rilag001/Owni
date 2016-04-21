package se.rickylagerkvist.owni;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Ricky on 2016-04-21.
 */
public class Owni extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }
}
