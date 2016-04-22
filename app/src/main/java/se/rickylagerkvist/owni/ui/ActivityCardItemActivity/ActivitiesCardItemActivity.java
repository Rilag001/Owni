package se.rickylagerkvist.owni.ui.ActivityCardItemActivity;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Locale;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.model.ActivityCard;
import se.rickylagerkvist.owni.model.ActivityCardItem;
import se.rickylagerkvist.owni.ui.loginAndCreateUser.LoginActivity;
import se.rickylagerkvist.owni.utils.Constants;

public class ActivitiesCardItemActivity extends AppCompatActivity {

    // Views, layout & adapters
    private ActivityCard mActivityCard;
    private ListView mIOweListView, mXOwesListView;
    private TextView mIoweTitle, mXOwesTitle, mBalance;
    private ImageView mRoundBalance;
    private ActivitiesCardItemAdapter mIoweXAdapter, mXowesMeAdapter;
    private Menu mMenu;
    private Toolbar mToolbar;

    // Firebase
    private Firebase mActivitiesCardRef, mActivitiesCardListItemRef, mActivitiesCardListItemIOweRef, mActivitiesCardListItemXOwesRef;
    private ValueEventListener mActivitiesCardRefListener, mActivitiesCardListItemRefListener;
    private Firebase.AuthStateListener mFirebaseRefAuthListener;

    //Strings
    private String mActivitiesCardId, mActivitiesCardName;
    private String mUserUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avtivities_card_item);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mUserUid = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("USERUID", "defaultStringIfNothingFound");

        // ListViews
        mIOweListView = (ListView) findViewById(R.id.i_owe_activities_list);
        mXOwesListView = (ListView) findViewById(R.id.activities_owe_me_list);
        
        //TextView
        mIoweTitle = (TextView) findViewById(R.id.i_owe_activities_list_title);
        mXOwesTitle = (TextView) findViewById(R.id.activities_owe_me_list_title);
        mBalance = (TextView) findViewById(R.id.activities_card_items_balance);

        // Round ImageVier mRoundBalance
        mRoundBalance = (ImageView) findViewById(R.id.round_balance_activities);;

        Intent intent = this.getIntent();
        mActivitiesCardId = intent.getStringExtra("ACTIVITIESCARD_ITEM_ID");
        if (mActivitiesCardId == null) {
            finish();
            return;
        }

        // PeopleCard ref
        mActivitiesCardRef = new Firebase(Constants.FIREBASE_URL_ACTIVITIES + "/"
                + mUserUid).child(mActivitiesCardId);

        // PeopleCard items refs
        mActivitiesCardListItemRef = new Firebase(Constants.FIREBASE_URL_ACTIVITIES_ITEMS
                + "/" + mUserUid).child(mActivitiesCardId);
        // iowe child
        mActivitiesCardListItemIOweRef = new Firebase(Constants.FIREBASE_URL_ACTIVITIES_ITEMS
                + "/" + mUserUid).child(mActivitiesCardId).child("iowe");
        // xowes child
        mActivitiesCardListItemXOwesRef = new Firebase(Constants.FIREBASE_URL_ACTIVITIES_ITEMS
                + "/" + mUserUid).child(mActivitiesCardId).child("xowes");


        // listens for login state, if the user is logged out open LoginActivity
        mFirebaseRefAuthListener = mActivitiesCardRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData == null) {
                    Intent intent = new Intent(ActivitiesCardItemActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        // I owe List adapter
        mIoweXAdapter = new ActivitiesCardItemAdapter(ActivitiesCardItemActivity.this, ActivityCardItem.class,
                R.layout.card_activities_item, mActivitiesCardListItemIOweRef);
        mIOweListView.setAdapter(mIoweXAdapter);


        // Other list owe adapter
        mXowesMeAdapter = new ActivitiesCardItemAdapter(ActivitiesCardItemActivity.this, ActivityCardItem.class,
                R.layout.card_activities_item, mActivitiesCardListItemXOwesRef);
        mXOwesListView.setAdapter(mXowesMeAdapter);

        // set mToolbar titel and textView titel
        mActivitiesCardRefListener = mActivitiesCardRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get ActivityCard
                ActivityCard activityCard = dataSnapshot.getValue(ActivityCard.class);
                // set mActivityCard to ActivityCard for ev later use
                mActivityCard = activityCard;

                // first name of ActivityCard IF ActivityCard != null
                if (activityCard != null) {
                    // set mToolbar titel to PeopleCards name
                    mToolbar.setTitle(activityCard.getNameOfActivity());
                } else {
                    finish();
                    return;
                }

                // set title to include name
                mIoweTitle.setText(getResources().getString(R.string.i_owe_person) + " other people");
                mXOwesTitle.setText("Other people owe me" + " " + getResources().getString(R.string.person_owes_me));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // balance, the sum of getAmount if getValue == "Kr"
        mActivitiesCardListItemRefListener = mActivitiesCardListItemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int iOweSum = 0;
                int xOwesSum = 0;
                int iOweAndXOwesBalance = 0;
                int displayNr = 0;

                int nrOfItems = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot activitiesCardItem : snapshot.getChildren()) {

                        ActivityCardItem item = activitiesCardItem.getValue(ActivityCardItem.class);

                        if (item.isiOwe() && item.getTypeOfValue().equalsIgnoreCase(getLocalCurrency())) {
                            iOweSum = iOweSum + item.getAmount();
                        } else if (!item.isiOwe() && item.getTypeOfValue().equalsIgnoreCase(getLocalCurrency())) {
                            xOwesSum = xOwesSum + item.getAmount();
                        }

                        iOweAndXOwesBalance = iOweSum - xOwesSum;

                        // gets the positive value if the number is negative (so the display do not show: "X owes you -500 kr")
                        if (iOweAndXOwesBalance < 0) {
                            displayNr = -iOweAndXOwesBalance;
                        } else {
                            displayNr = iOweAndXOwesBalance;
                        }

                        // set mBalance text and round balance image
                        if (iOweAndXOwesBalance == 0) {
                            mBalance.setText(getString(R.string.you_are_squared) + " " + 0 + " " + getString(R.string.currency));
                            mRoundBalance.setImageResource(R.drawable.round_blue);
                        } else if (iOweAndXOwesBalance < 0) {
                            mBalance.setText("Other people owe you" + " " + displayNr + " " + getString(R.string.currency));
                            mRoundBalance.setImageResource(R.drawable.round_green);
                        } else if (iOweAndXOwesBalance > 0) {
                            mBalance.setText("You owe other people" + " " + displayNr + " " + getString(R.string.currency));
                            mRoundBalance.setImageResource(R.drawable.round_red);
                        }

                        nrOfItems++;
                    }
                }
                // set balance and nr of items to the peopleCard at mPeopleCardRef
                if (mActivityCard != null) {
                    mActivityCard.setBalance(iOweAndXOwesBalance);
                    mActivityCard.setNumberOfItems(nrOfItems);
                    mActivitiesCardRef.setValue(mActivityCard);
                } else {
                    finish();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // Delete items in listViews
        mIOweListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                ActivityCardItem mActivitiesCard = mIoweXAdapter.getItem(position);

                String iOweOfXOwe = null;
                if (mActivitiesCard.isiOwe()) {
                    iOweOfXOwe = "iowe";
                } else if (!mActivitiesCard.isiOwe()) {
                    iOweOfXOwe = "xowes";
                }

                String mActivitiesCardItemId = mIoweXAdapter.getRef(position).getKey();

                DialogFragment dialog = DeleteActivityCardItemDialog.newInstance(mActivitiesCardId, mActivitiesCardItemId, iOweOfXOwe);
                dialog.show(ActivitiesCardItemActivity.this.getFragmentManager(), "DeleteCardItemDialog");
            }
        });
        mXOwesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                ActivityCardItem mActivitiesCard = mXowesMeAdapter.getItem(position);

                String iOweOfXOwe = null;
                if (mActivitiesCard.isiOwe()) {
                    iOweOfXOwe = "iowe";
                } else if (!mActivitiesCard.isiOwe()) {
                    iOweOfXOwe = "xowes";
                }
                String mActivitiesCardItemId = mXowesMeAdapter.getRef(position).getKey();

                DialogFragment dialog = DeleteActivityCardItemDialog.newInstance(mActivitiesCardId, mActivitiesCardItemId, iOweOfXOwe);
                dialog.show(ActivitiesCardItemActivity.this.getFragmentManager(), "DeleteCardItemDialog");
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // clean adapter and removeEventListener
    @Override
    public void onDestroy() {
        super.onDestroy();
        mIoweXAdapter.cleanup();
        mXowesMeAdapter.cleanup();
        mActivitiesCardRef.removeEventListener(mActivitiesCardRefListener);
        mActivitiesCardListItemRef.removeEventListener(mActivitiesCardListItemRefListener);
        mActivitiesCardRef.removeAuthStateListener(mFirebaseRefAuthListener);
    }

    // Open dialog to add new PeopleCard
    public void showAddPeopleCardItemDialog(View view) {
        DialogFragment dialog = AddActivitiesCardItemDialog.newInstance(mActivitiesCardId);
        dialog.show(ActivitiesCardItemActivity.this.getFragmentManager(), "AddPeopleCardItemDialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_people_item, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete_card_and_items) {
            DialogFragment dialog = DeleteActivitiesCardAndItemsDialog.newInstance(mActivitiesCardId);
            dialog.show(ActivitiesCardItemActivity.this.getFragmentManager(), "DeleteCardAndItemsDialog");
        }

        return super.onOptionsItemSelected(item);
    }

    // returns the currency of languages setting (can be filled out with more) balance are calculated on local
    public String getLocalCurrency() {
        String localCurrency = null;

        if (Locale.getDefault().toString().equals("en_US")) {
            localCurrency = "dollar";
        } else if (Locale.getDefault().toString().equals("sv_SE")) {
            localCurrency = "kr";
        } else {
            localCurrency = "dollar";
        }
        return localCurrency;
    }

    // opens swish if it is installed, otherwise show Toast
    public void openSwish(View view) {
        String appPackageName = "se.bankgirot.swish";
        String appName = "Swish";
        String appUrl = "https://play.google.com/store/apps/details?id=se.bankgirot.swish";

        openAppOrOpenSnackbarWithInstallAction(appPackageName, appName, appUrl, view);
    }

    // generic open app with intent or show snackbar with install action
    private void openAppOrOpenSnackbarWithInstallAction(String appPackageName, String appName, final String appUrl, View view) {

        PackageManager pm = getBaseContext().getPackageManager();
        Intent appStartIntent = pm.getLaunchIntentForPackage(appPackageName);
        if (null != appStartIntent) {
            getBaseContext().startActivity(appStartIntent);
        } else {
            Snackbar snackbar = Snackbar
                    .make(view,  appName + " is not installed.", Snackbar.LENGTH_LONG)
                    .setAction("INSTALL " + appName.toUpperCase(), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            goToUrl(appUrl);
                        }
                    });
            snackbar.show();
        }

    }

    // generic go to Url
    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

}
