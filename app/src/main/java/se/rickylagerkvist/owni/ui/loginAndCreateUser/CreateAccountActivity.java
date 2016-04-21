package se.rickylagerkvist.owni.ui.loginAndCreateUser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.model.FireBaseUser;
import se.rickylagerkvist.owni.utils.Constants;

// Background in xml from: https://unsplash.com/photos/8mqOw4DBBSg (licensed under Creative Commons Zero)
public class CreateAccountActivity extends AppCompatActivity {

    private static final String LOG_TAG = CreateAccountActivity.class.getSimpleName();
    private ProgressDialog mAuthProgressDialog;
    private EditText mEditTextUsernameCreate, mEditTextEmailCreate, mEditTextPasswordCreate;
    private Firebase mFirebaseRef;
    private String mUserName, mUserEmail, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Set Firebase Context and connection String
        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);

        initializeScreen();
    }

    // XML and progress dialog
    private void initializeScreen() {
        mEditTextUsernameCreate = (EditText) findViewById(R.id.edit_text_username_create);
        mEditTextEmailCreate = (EditText) findViewById(R.id.edit_text_email_create);
        mEditTextPasswordCreate = (EditText) findViewById(R.id.edit_text_password_create);

        // Setup the progress dialog that is displayed later when authenticating with Firebase
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getResources().getString(R.string.progress_dialog_creating_user_with_firebase));
        mAuthProgressDialog.setCancelable(false);
    }

    // Open LoginActivity when user taps on "Sign in" textView
    public void onSignInPressed(android.view.View view) {
        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Create new account using Firebase email/password provider
    public void onCreateAccountPressed(android.view.View view) {

        mUserName = mEditTextUsernameCreate.getText().toString();
        mUserEmail = mEditTextEmailCreate.getText().toString().toLowerCase();
        mPassword = mEditTextPasswordCreate.getText().toString();

        // Check that email and user name are valid
        boolean validEmail = isEmailValid(mUserEmail);
        boolean validUserName = isUserNameValid(mUserName);
        boolean validPassword = isPasswordValid(mPassword);
        if (!validEmail || !validUserName || !validPassword) return;

        // If everything was valid show the progress dialog to indicate that
        // account creation has started
        mAuthProgressDialog.show();

        // Create new user with specified email and password
        mFirebaseRef.createUser(mUserEmail, mPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                // Dismiss the progress dialog
                mAuthProgressDialog.dismiss();
                Log.i(LOG_TAG, getString(R.string.log_message_auth_successful));

                //createUserInFirebaseHelper(uid);
                String uid = (String) result.get("uid");
                createUserInFirebaseHelper(uid);

                Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // Error occurred, log the error and dismiss the progress dialog
                Log.d(LOG_TAG, getString(R.string.log_error_occurred) +
                        firebaseError);
                mAuthProgressDialog.dismiss();
                /* Display the appropriate error message */
                if (firebaseError.getCode() == FirebaseError.EMAIL_TAKEN) {
                    mEditTextEmailCreate.setError(getString(R.string.error_email_taken));
                } else {
                    showErrorToast(firebaseError.getMessage());
                }
            }
        });
    }

    // Creates a new user in Firebase with the Java FirebaseUser
    private void createUserInFirebaseHelper(String uid) {

        // Unique locations for new user
        final Firebase userLocation = new Firebase(Constants.FIREBASE_URL_USERS).child(uid);
        final Firebase peopleLocation = new Firebase(Constants.FIREBASE_URL_PEOPLE).child(uid);
        final Firebase activitiesLocation = new Firebase(Constants.FIREBASE_URL_ACTIVITIES).child(uid);
        final Firebase peopleItemsLocation = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS).child(uid);
        final Firebase activitiesItemsLocation = new Firebase(Constants.FIREBASE_URL_ACTIVITIES_ITEMS).child(uid);


        userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If there is no user, create one
                if (dataSnapshot.getValue() == null) {
                    // Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap
                    HashMap<String, Object> timestampJoined = new HashMap<>();
                    timestampJoined.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    // Create and populate locations for new user
                    FireBaseUser newUser = new FireBaseUser(mUserName, mUserEmail, timestampJoined);
                    userLocation.setValue(newUser);
                    peopleLocation.setValue("null");
                    activitiesLocation.setValue("null");
                    peopleItemsLocation.setValue("null");
                    activitiesItemsLocation.setValue("null");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(LOG_TAG, getString(R.string.log_error_occurred) + firebaseError.getMessage());
            }
        });

    }

    // checks that the password is min 6 char long
    private boolean isPasswordValid(String password) {
        if (password.length() < 6) {
            mEditTextPasswordCreate.setError(getResources().getString(R.string.error_invalid_password_not_valid));
            return false;
        }
        return true;
    }

    // Checks if the user name is empty
    private boolean isUserNameValid(String userName) {
        if (userName.equals("")) {
            mEditTextUsernameCreate.setError(getResources().getString(R.string.error_cannot_be_empty));
            return false;
        }
        return true;
    }

    // Checks if the email is empty of not email, if so set error "Invalid email"
    private boolean isEmailValid(String email) {
        boolean isGoodEmail = (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());

        if (!isGoodEmail) {
            mEditTextEmailCreate.setError(getResources().getString(R.string.error_invalid_email_not_valid));
            return false;
        }
        return isGoodEmail;
    }

    // Show error toast to users
    private void showErrorToast(String message) {
        Toast.makeText(CreateAccountActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
