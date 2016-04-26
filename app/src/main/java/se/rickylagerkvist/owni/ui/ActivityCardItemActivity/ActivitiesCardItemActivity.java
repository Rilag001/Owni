package se.rickylagerkvist.owni.ui.ActivityCardItemActivity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.Utils;
import se.rickylagerkvist.owni.model.ActivityCard;
import se.rickylagerkvist.owni.model.ActivityCardItem;
import se.rickylagerkvist.owni.ui.loginAndCreateUser.LoginActivity;
import se.rickylagerkvist.owni.utils.Constants;

public class ActivitiesCardItemActivity extends AppCompatActivity {

    // Views, layout & adapters
    private ActivityCard mActivityCard;
    private TextView mIoweTitle, mXOwesTitle, mBalance;
    private ImageView mRoundBalance;
    private ActivitiesCardItemAdapter mIoweXAdapter, mXowesMeAdapter;
    private Toolbar mToolbar;

    // Firebase
    private Firebase mActivitiesCardRef;
    private Firebase mActivitiesCardListItemRef;
    private ValueEventListener mActivitiesCardRefListener, mActivitiesCardListItemRefListener;
    private Firebase.AuthStateListener mFirebaseRefAuthListener;

    //Strings
    private String mActivitiesCardId, mCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avtivities_card_item);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // get mUserUid and mCurrency
        String userUid = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("USERUID", "defaultStringIfNothingFound");
        mCurrency = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("CURRENCY", "Select your currency");

        // ListViews
        ListView IOweListView = (ListView) findViewById(R.id.i_owe_activities_list);
        ListView XOwesListView = (ListView) findViewById(R.id.activities_owe_me_list);

        //TextView
        mIoweTitle = (TextView) findViewById(R.id.i_owe_activities_list_title);
        mXOwesTitle = (TextView) findViewById(R.id.activities_owe_me_list_title);
        mBalance = (TextView) findViewById(R.id.activities_card_items_balance);

        // Round ImageVier mRoundBalance
        mRoundBalance = (ImageView) findViewById(R.id.round_balance_activities);

        Intent intent = this.getIntent();
        mActivitiesCardId = intent.getStringExtra("ACTIVITIESCARD_ITEM_ID");
        if (mActivitiesCardId == null) {
            finish();
            return;
        }

        // PeopleCard ref
        mActivitiesCardRef = new Firebase(Constants.FIREBASE_URL_ACTIVITIES + "/"
                + userUid).child(mActivitiesCardId);

        // PeopleCard items refs
        mActivitiesCardListItemRef = new Firebase(Constants.FIREBASE_URL_ACTIVITIES_ITEMS
                + "/" + userUid).child(mActivitiesCardId);
        // iowe child
        Firebase activitiesCardListItemIOweRef = new Firebase(Constants.FIREBASE_URL_ACTIVITIES_ITEMS
                + "/" + userUid).child(mActivitiesCardId).child("iowe");
        // xowes child
        Firebase activitiesCardListItemXOwesRef = new Firebase(Constants.FIREBASE_URL_ACTIVITIES_ITEMS
                + "/" + userUid).child(mActivitiesCardId).child("xowes");


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
                R.layout.card_activities_item, activitiesCardListItemIOweRef);
        assert IOweListView != null;
        IOweListView.setAdapter(mIoweXAdapter);


        // Other list owe adapter
        mXowesMeAdapter = new ActivitiesCardItemAdapter(ActivitiesCardItemActivity.this, ActivityCardItem.class,
                R.layout.card_activities_item, activitiesCardListItemXOwesRef);
        assert XOwesListView != null;
        XOwesListView.setAdapter(mXowesMeAdapter);

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
                mIoweTitle.setText(getResources().getString(R.string.i_owe_other_people));
                mXOwesTitle.setText(getResources().getString(R.string.other_people_owe_me));
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
                int displayNr;
                int nrOfItems = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot activitiesCardItem : snapshot.getChildren()) {

                        ActivityCardItem item = activitiesCardItem.getValue(ActivityCardItem.class);

                        if (item.isiOwe() && item.getTypeOfValue().equalsIgnoreCase(mCurrency)) {
                            iOweSum = iOweSum + item.getAmount();
                        } else if (!item.isiOwe() && item.getTypeOfValue().equalsIgnoreCase(mCurrency)) {
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
                            mBalance.setText(getString(R.string.you_are_squared));
                            mRoundBalance.setImageResource(R.drawable.round_blue);
                        } else if (iOweAndXOwesBalance < 0) {
                            mBalance.setText(getString(R.string.other_people_owe_me_amount_currency, displayNr, mCurrency));
                            mRoundBalance.setImageResource(R.drawable.round_green);
                        } else if (iOweAndXOwesBalance > 0) {
                            mBalance.setText(getString(R.string.i_owe_other_people_amount_currency, displayNr, mCurrency));
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
        IOweListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        XOwesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    // opens swish if it is installed, otherwise show Toast
    public void openSwish(View view) {
        String appPackageName = "se.bankgirot.swish";
        String appName = "Swish";
        String appUrl = "https://play.google.com/store/apps/details?id=se.bankgirot.swish";

        Utils.openAppOrOpenSnackbarWithInstallAction(appPackageName, appName, appUrl, view, getBaseContext());
    }

}
