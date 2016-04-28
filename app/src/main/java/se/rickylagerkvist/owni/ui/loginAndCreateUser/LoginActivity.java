package se.rickylagerkvist.owni.ui.loginAndCreateUser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.ui.SelectCurrencyActivity;
import se.rickylagerkvist.owni.utils.Constants;

/*
I quickly realised I needed user accounts, otherwise all users would get the same data.
This activity and CreateAccountActivity i built on boilerplate code from the two following sites:
https://www.firebase.com/docs/web/guide/login/password.html &
https://www.udacity.com/course/firebase-essentials-for-android--ud009
Background from: https://unsplash.com/photos/8mqOw4DBBSg (licensed under Creative Commons Zero)
*/

public class LoginActivity extends AppCompatActivity {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    // A dialog that is presented until the Firebase authentication finished.
    private ProgressDialog mAuthProgressDialog;
    private Firebase mFirebaseRef;
    private EditText mEditTextEmailInput, mEditTextPasswordInput;
    protected String mUserUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set Firebase Context and connection String
        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);

        initializeScreen();

        // Call signInPassword() when user taps "Done" keyboard action
        mEditTextPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    signInPassword();
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    // Sign in with Password provider when user clicks "Sign in" button
    public void onSignInPressed(android.view.View view) {
        signInPassword();
    }

    // Open CreateAccountActivity when user taps on "Sign up" TextView
    public void onSignUpPressed(android.view.View view) {
        Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
        startActivity(intent);
    }


    private void signInPassword() {
        String email = mEditTextEmailInput.getText().toString();
        String password = mEditTextPasswordInput.getText().toString();

        // If email and password are not empty show progress dialog and try to authenticate
        if (email.equals("")) {
            mEditTextEmailInput.setError(getString(R.string.error_cannot_be_empty));
            return;
        }

        if (password.equals("")) {
            mEditTextPasswordInput.setError(getString(R.string.error_cannot_be_empty));
            return;
        }
        mAuthProgressDialog.show();
        mFirebaseRef.authWithPassword(email, password, new MyAuthResultHandler(Constants.PASSWORD_PROVIDER));

    }

    // Helper method that makes sure a user is created if the use logs in with Firebase's email/password provider.
    private class MyAuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public MyAuthResultHandler(String provider) {
            this.provider = provider;
        }

        // On successful authentication call setAuthenticatedUser if it was not already called in
        @Override
        public void onAuthenticated(AuthData authData) {
            mAuthProgressDialog.dismiss();
            Log.i(LOG_TAG, provider + " " + getString(R.string.log_message_auth_successful));

            if (authData != null){

                mUserUid = authData.getUid();
                // save mUserUid to sharedPref
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("USERUID", mUserUid).apply();

                // Go to main activity
                Intent intent = new Intent(LoginActivity.this, SelectCurrencyActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }

        // Error messages
        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {

            // closes the mAuthProgressDialog if there is an error
            mAuthProgressDialog.dismiss();

            switch (firebaseError.getCode()) {
                case FirebaseError.INVALID_EMAIL:
                    mEditTextEmailInput.setError(getString(R.string.invalid_email));
                case FirebaseError.USER_DOES_NOT_EXIST:
                    mEditTextEmailInput.setError(getString(R.string.error_message_email_issue));
                    break;
                case FirebaseError.INVALID_PASSWORD:
                    mEditTextPasswordInput.setError(firebaseError.getMessage());
                    break;
                case FirebaseError.NETWORK_ERROR:
                    showErrorToast(getString(R.string.error_message_failed_sign_in_no_network));
                    break;
                default:
                    showErrorToast(firebaseError.toString());
            }
        }
    }

    // Show error toast to users
    private void showErrorToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void initializeScreen() {
        // user input EditTexts
        mEditTextEmailInput = (EditText) findViewById(R.id.edit_text_email);
        mEditTextPasswordInput = (EditText) findViewById(R.id.edit_text_password);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getString(R.string.progress_dialog_authenticating_with_firebase));
        mAuthProgressDialog.setCancelable(false);
    }
}
