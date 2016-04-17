package se.rickylagerkvist.owni.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import se.rickylagerkvist.owni.ui.loginAndCreateUser.LoginActivity;

public class StartCheckLogIfLogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // fetch mEncodedEmail
        String mEncodedEmail = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("ENCODEDEMAIL", "defaultStringIfNothingFound");

        // if mEncodedEmail has default value start LoginActivity, else start MainActivity
        Intent intent;
        if (mEncodedEmail.equals("defaultStringIfNothingFound")){
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
