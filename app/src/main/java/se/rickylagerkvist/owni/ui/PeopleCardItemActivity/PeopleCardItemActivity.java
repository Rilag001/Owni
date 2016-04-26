package se.rickylagerkvist.owni.ui.PeopleCardItemActivity;

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
import se.rickylagerkvist.owni.model.PeopleCard;
import se.rickylagerkvist.owni.model.PeopleCardItem;
import se.rickylagerkvist.owni.ui.loginAndCreateUser.LoginActivity;
import se.rickylagerkvist.owni.utils.Constants;

public class PeopleCardItemActivity extends AppCompatActivity {

    // Views, layout & adapters
    private PeopleCard mPeopleCard;
    private TextView mIoweTitle, mXOwesTitle, mBalance;
    private ImageView mRoundBalance;
    private PeopleCardItemAdapter mIoweXAdapter, mXowesMeAdapter;
    private Toolbar mToolbar;

    // Firebase
    private Firebase mPeopleCardRef;
    private Firebase mPeopleCardListItemRef;
    private ValueEventListener mPeopleCardRefListener, mPeopleCardListItemRefListener;
    private Firebase.AuthStateListener mFirebaseRefAuthListener;

    //Strings
    private String mPeopleCardId, mPeopleCardFirstName, mCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_card_item);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //get userUid & mCurrency
        String userUid = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("USERUID", "defaultStringIfNothingFound");
        mCurrency = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("CURRENCY", "Select your currency");

        // init ListViews
        ListView IOweListView = (ListView) findViewById(R.id.i_owe_people_list);
        ListView XOwesListView = (ListView) findViewById(R.id.people_owe_me_list);

        //TextView
        mIoweTitle = (TextView) findViewById(R.id.i_owe_people_list_title);
        mXOwesTitle = (TextView) findViewById(R.id.people_owe_me_list_title);
        mBalance = (TextView) findViewById(R.id.people_card_items_balance);

        // Round ImageVier mRoundBalance
        mRoundBalance = (ImageView) findViewById(R.id.round_balance_people);

        Intent intent = this.getIntent();
        mPeopleCardId = intent.getStringExtra("PEOPLECARD_ITEM_ID");
        if (mPeopleCardId == null) {
            finish();
            return;
        }

        // PeopleCard ref
        mPeopleCardRef = new Firebase(Constants.FIREBASE_URL_PEOPLE + "/"
                + userUid).child(mPeopleCardId);

        // PeopleCard items refs
        mPeopleCardListItemRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS
                + "/" + userUid).child(mPeopleCardId);
        // iowe child
        Firebase peopleCardListItemIOweRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS
                + "/" + userUid).child(mPeopleCardId).child("iowe");
        // xowes child
        Firebase peopleCardListItemXOwesRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS
                + "/" + userUid).child(mPeopleCardId).child("xowes");


        // listens for login state, if the user is logged out open LoginActivity
        mFirebaseRefAuthListener = mPeopleCardRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData == null) {
                    Intent intent = new Intent(PeopleCardItemActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        // I owe List adapter
        mIoweXAdapter = new PeopleCardItemAdapter(PeopleCardItemActivity.this, PeopleCardItem.class,
                R.layout.card_people_item, peopleCardListItemIOweRef);
        assert IOweListView != null;
        IOweListView.setAdapter(mIoweXAdapter);

        // Other list owe adapter
        mXowesMeAdapter = new PeopleCardItemAdapter(PeopleCardItemActivity.this, PeopleCardItem.class,
                R.layout.card_people_item, peopleCardListItemXOwesRef);
        assert XOwesListView != null;
        XOwesListView.setAdapter(mXowesMeAdapter);


        // set mToolbar titel and textView titel
        mPeopleCardRefListener = mPeopleCardRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get Peoplecard
                PeopleCard peopleCard = dataSnapshot.getValue(PeopleCard.class);
                // set mPeopleCard to peoplecard for ev later use
                mPeopleCard = peopleCard;

                // first name of peoplecard IF peoplecard != null
                if (peopleCard != null) {
                    mPeopleCardFirstName = peopleCard.getName().split(" ", 2)[0];
                } else {
                    finish();
                    return;
                }

                // set mToolbar titel to PeopleCards name
                mToolbar.setTitle(peopleCard.getName());

                // set title to include name
                mIoweTitle.setText(getString(R.string.i_owe_person, mPeopleCardFirstName));
                mXOwesTitle.setText(getString(R.string.person_owes_me,mPeopleCardFirstName));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // balance, the sum of getAmount if getValue == "Kr"
        mPeopleCardListItemRefListener = mPeopleCardListItemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int iOweSum = 0;
                int xOwesSum = 0;
                int iOweAndXOwesBalance = 0;
                int displayNr;
                int nrOfItems = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot peopleCardItem : snapshot.getChildren()) {

                        PeopleCardItem item = peopleCardItem.getValue(PeopleCardItem.class);

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
                            mBalance.setText(getString(R.string.person_owes_me_amount_currency, mPeopleCardFirstName, displayNr, mCurrency));
                            mRoundBalance.setImageResource(R.drawable.round_green);
                        } else if (iOweAndXOwesBalance > 0) {
                            mBalance.setText(getString(R.string.i_owe_person_amount_currency, mPeopleCardFirstName, displayNr, mCurrency));
                            mRoundBalance.setImageResource(R.drawable.round_red);
                        }

                        nrOfItems++;
                    }
                }
                // set balance and nr of items to the peopleCard at mPeopleCardRef
                if (mPeopleCard != null) {
                    mPeopleCard.setBalance(iOweAndXOwesBalance);
                    mPeopleCard.setNumberOfItems(nrOfItems);
                    mPeopleCardRef.setValue(mPeopleCard);
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
                PeopleCardItem mPeopleCard = mIoweXAdapter.getItem(position);

                String iOweOfXOwe = null;
                if (mPeopleCard.isiOwe()) {
                    iOweOfXOwe = "iowe";
                } else if (!mPeopleCard.isiOwe()) {
                    iOweOfXOwe = "xowes";
                }

                String mPeopleCardItemId = mIoweXAdapter.getRef(position).getKey();

                DialogFragment dialog = DeleteCardItemDialog.newInstance(mPeopleCardId, mPeopleCardItemId, iOweOfXOwe);
                dialog.show(PeopleCardItemActivity.this.getFragmentManager(), "DeleteCardItemDialog");
            }
        });
        XOwesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                PeopleCardItem mPeopleCard = mXowesMeAdapter.getItem(position);

                String iOweOfXOwe = null;
                if (mPeopleCard.isiOwe()) {
                    iOweOfXOwe = "iowe";
                } else if (!mPeopleCard.isiOwe()) {
                    iOweOfXOwe = "xowes";
                }
                String mPeopleCardItemId = mXowesMeAdapter.getRef(position).getKey();

                DialogFragment dialog = DeleteCardItemDialog.newInstance(mPeopleCardId, mPeopleCardItemId, iOweOfXOwe);
                dialog.show(PeopleCardItemActivity.this.getFragmentManager(), "DeleteCardItemDialog");
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
        mPeopleCardRef.removeEventListener(mPeopleCardRefListener);
        mPeopleCardListItemRef.removeEventListener(mPeopleCardListItemRefListener);
        mPeopleCardRef.removeAuthStateListener(mFirebaseRefAuthListener);
    }

    // Open dialog to add new PeopleCard
    public void showAddPeopleCardItemDialog(View view) {
        DialogFragment dialog = AddPeopleCardItemDialog.newInstance(mPeopleCardId, mPeopleCardFirstName);
        dialog.show(PeopleCardItemActivity.this.getFragmentManager(), "AddPeopleCardItemDialog");
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
            DialogFragment dialog = DeleteCardAndItemsDialog.newInstance(mPeopleCardId);
            dialog.show(PeopleCardItemActivity.this.getFragmentManager(), "DeleteCardAndItemsDialog");
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
