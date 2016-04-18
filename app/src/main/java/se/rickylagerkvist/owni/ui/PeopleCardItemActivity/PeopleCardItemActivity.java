package se.rickylagerkvist.owni.ui.PeopleCardItemActivity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
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
import se.rickylagerkvist.owni.model.PeopleCard;
import se.rickylagerkvist.owni.model.PeopleCardItem;
import se.rickylagerkvist.owni.ui.loginAndCreateUser.LoginActivity;
import se.rickylagerkvist.owni.utils.Constants;

public class PeopleCardItemActivity extends AppCompatActivity {

    // Views, layout & adapters
    private PeopleCard mPeopleCard;
    private FloatingActionButton mFab;
    private ListView mIOweListView, mXOwesListView;
    private TextView mIoweTitle, mXOwesTitle, mBalance;
    private ImageView mRoundBalance;
    private PeopleCardItemAdapter mIoweXAdapter, mXowesMeAdapter;
    private Menu mMenu;
    private Toolbar mToolbar;

    // Firebase
    private Firebase mPeopleCardRef, mPeopleCardListItemRef, mPeopleCardListItemIOweRef, mPeopleCardListItemXOwesRef;
    private ValueEventListener mPeopleCardRefListener, mPeopleCardListItemRefListener;
    private Firebase.AuthStateListener mFirebaseRefAuthListener;

    //Strings
    private String mPeopleCardId, mPeopleCardFirstName;
    private String mEncodedEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_card_item);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mEncodedEmail = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("ENCODEDEMAIL", "defaultStringIfNothingFound");

        // ListViews
        mIOweListView = (ListView) findViewById(R.id.i_owe_people_list);
        mXOwesListView = (ListView) findViewById(R.id.people_owe_me_list);

        //TextView
        mIoweTitle = (TextView) findViewById(R.id.i_owe_people_list_title);
        mXOwesTitle = (TextView) findViewById(R.id.people_owe_me_list_title);
        mBalance = (TextView) findViewById(R.id.people_card_items_balance);

        // Round ImageVier mRoundBalance
        mRoundBalance = (ImageView) findViewById(R.id.round_balance);

        Intent intent = this.getIntent();
        mPeopleCardId = intent.getStringExtra("PEOPLECARD_ITEM_ID");
        if (mPeopleCardId == null) {
            finish();
            return;
        }

        // PeopleCard ref
        mPeopleCardRef = new Firebase(Constants.FIREBASE_URL_PEOPLE + "/"
                + mEncodedEmail).child(mPeopleCardId);

        // PeopleCard items refs
        mPeopleCardListItemRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS
                + "/" + mEncodedEmail).child(mPeopleCardId);
            // iowe child
        mPeopleCardListItemIOweRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS
                + "/" + mEncodedEmail).child(mPeopleCardId).child("iowe");
            // xowes child
        mPeopleCardListItemXOwesRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS
                + "/" + mEncodedEmail).child(mPeopleCardId).child("xowes");


        // listens for login state, if the user is logged out open LoginActivity
        mFirebaseRefAuthListener = mPeopleCardRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData == null){
                    Intent intent = new Intent(PeopleCardItemActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        // I owe List adapter
        mIoweXAdapter = new PeopleCardItemAdapter(PeopleCardItemActivity.this, PeopleCardItem.class,
                R.layout.card_people_item, mPeopleCardListItemIOweRef);
        mIOweListView.setAdapter(mIoweXAdapter);

        // Other list owe adapter
        mXowesMeAdapter = new PeopleCardItemAdapter(PeopleCardItemActivity.this, PeopleCardItem.class,
                R.layout.card_people_item, mPeopleCardListItemXOwesRef);
        mXOwesListView.setAdapter(mXowesMeAdapter);


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
                mIoweTitle.setText(getResources().getString(R.string.i_owe_person) + " " + mPeopleCardFirstName);
                mXOwesTitle.setText(mPeopleCardFirstName + " " + getResources().getString(R.string.person_owes_me));
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
                int displayNr = 0;

                int nrOfItems = 0;

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    for (DataSnapshot peopleCardItem: snapshot.getChildren()){

                        PeopleCardItem item = peopleCardItem.getValue(PeopleCardItem.class);


                        if (item.isiOwe() && item.getTypeOfValue().equalsIgnoreCase(getLocalCurrency())){
                            iOweSum = iOweSum + item.getAmount();
                        } else if (!item.isiOwe() && item.getTypeOfValue().equalsIgnoreCase(getLocalCurrency())) {
                            xOwesSum = xOwesSum + item.getAmount();
                        }

                        iOweAndXOwesBalance = iOweSum - xOwesSum;

                        // gets the positive value if the number is negative (so the display do not show: "X owes you -500 kr")
                        if (iOweAndXOwesBalance < 0){
                            displayNr = -iOweAndXOwesBalance;
                        } else {
                            displayNr = iOweAndXOwesBalance;
                        }


                        // set mBalance text and round balance image
                        if (iOweAndXOwesBalance == 0){
                            mBalance.setText(getString(R.string.you_are_squared) + " " + 0 + " " + getString(R.string.currency));
                            mRoundBalance.setImageResource(R.drawable.round_blue);
                        } else if (iOweAndXOwesBalance < 0) {
                            mBalance.setText(mPeopleCardFirstName + " " + getString(R.string.owes_you) + " " + displayNr + " " + getString(R.string.currency));
                            mRoundBalance.setImageResource(R.drawable.round_green);
                        } else if (iOweAndXOwesBalance > 0) {
                            mBalance.setText(getString(R.string.you_owe) + " " + mPeopleCardFirstName + " " + displayNr + " " + getString(R.string.currency));
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
        mIOweListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                PeopleCardItem mPeopleCard = mIoweXAdapter.getItem(position);

                String iOweOfXOwe = null;
                if (mPeopleCard.isiOwe()){
                    iOweOfXOwe = "iowe";
                } else if (!mPeopleCard.isiOwe()) {
                    iOweOfXOwe = "xowes";
                }

                String mPeopleCardItemId = mIoweXAdapter.getRef(position).getKey();

                DialogFragment dialog = DeleteCardItemDialog.newInstance(mPeopleCardId, mPeopleCardItemId, iOweOfXOwe);
                dialog.show(PeopleCardItemActivity.this.getFragmentManager(), "DeleteCardItemDialog");
            }
        });
        mXOwesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                PeopleCardItem mPeopleCard = mXowesMeAdapter.getItem(position);

                String iOweOfXOwe = null;
                if (mPeopleCard.isiOwe()){
                    iOweOfXOwe = "iowe";
                } else if (!mPeopleCard.isiOwe()) {
                    iOweOfXOwe = "xowes";
                }
                String mPeopleCardItemId = mXowesMeAdapter.getRef(position).getKey();

                DialogFragment dialog = DeleteCardItemDialog.newInstance(mPeopleCardId, mPeopleCardItemId, iOweOfXOwe);
                dialog.show(PeopleCardItemActivity.this.getFragmentManager(), "DeleteCardItemDialog");
            }
        });

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPeopleCardItemDialog(view);
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
        this.mMenu = menu;
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

    // returns the currency of languages setting (can be filled out with more) balance are calculated on local
    public String getLocalCurrency (){
        String localCurrency = null;

        if(Locale.getDefault().toString().equals("en_US")){
            localCurrency = "dollar";
        } else if (Locale.getDefault().getLanguage().equals("sv_SE")) {
            localCurrency = "kr";
        } else {
            localCurrency = "dollar";
        }
        return localCurrency;
    }
}
