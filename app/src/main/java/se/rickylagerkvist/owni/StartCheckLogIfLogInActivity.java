package se.rickylagerkvist.owni;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import se.rickylagerkvist.owni.ui.MainActivity;
import se.rickylagerkvist.owni.ui.loginAndCreateUser.LoginActivity;

public class StartCheckLogIfLogInActivity extends AppCompatActivity {

    String mEncodedEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEncodedEmail = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("ENCODEDEMAIL", "defaultStringIfNothingFound");

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
