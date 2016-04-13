package se.rickylagerkvist.owni.ui.PeopleCardItemActivity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.model.PeopleCard;
import se.rickylagerkvist.owni.model.PeopleCardItem;
import se.rickylagerkvist.owni.utils.Constants;

public class PeopleCardItemActivity extends AppCompatActivity {

    private String mPeopleCardId, mPeopleCardFirstName;
    private PeopleCard mPeopleCard;
    private FloatingActionButton fab;
    private ListView mIOweListView, mXOwesListView;
    private TextView mIoweTitle, mXOwesTitle, mBalance;
    private ImageView mRoundBalance;
    private PeopleCardItemAdapter mIoweXAdapter, mXowesMeAdapter;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_card_item);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        final Firebase mPeopleCardRef = new Firebase(Constants.FIREBASE_URL_PEOPLE + "/"
                + Constants.KEY_ENCODED_EMAIL).child(mPeopleCardId);

        // PeopleCard items refs
        final Firebase mPeopleCardListItemRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS
                + "/" + Constants.KEY_ENCODED_EMAIL).child(mPeopleCardId);
            // iowe child
        Firebase mPeopleCardListItemIOweRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS
                + "/" + Constants.KEY_ENCODED_EMAIL).child(mPeopleCardId).child("iowe");
            // xowes child
        Firebase mPeopleCardListItemXOwesRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS
                + "/" + Constants.KEY_ENCODED_EMAIL).child(mPeopleCardId).child("xowes");

        // I owe List adapter
        mIoweXAdapter = new PeopleCardItemAdapter(PeopleCardItemActivity.this, PeopleCardItem.class,
                R.layout.card_people_item, mPeopleCardListItemIOweRef);
        mIOweListView.setAdapter(mIoweXAdapter);

        // Other list owe adapter
        mXowesMeAdapter = new PeopleCardItemAdapter(PeopleCardItemActivity.this, PeopleCardItem.class,
                R.layout.card_people_item, mPeopleCardListItemXOwesRef);
        mXOwesListView.setAdapter(mXowesMeAdapter);

        // set toolbar titel and textView titel
        mPeopleCardRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get Peoplecard
                PeopleCard peopleCard = dataSnapshot.getValue(PeopleCard.class);
                // set mPeopleCard to peoplecard for ev later use
                mPeopleCard = peopleCard;
                // first name of peoplecard
                if (peopleCard != null){
                    mPeopleCardFirstName = peopleCard.getName().split(" ", 2)[0];
                } else {
                    finish();
                    return;
                }

                // set toolbar titel to PeopleCards name
                toolbar.setTitle(peopleCard.getName());

                // set title to include name
                mIoweTitle.setText(getResources().getString(R.string.i_owe_person) + " " + mPeopleCardFirstName);
                mXOwesTitle.setText(mPeopleCardFirstName + " " + getResources().getString(R.string.person_owes_me));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // balance, the sum of getAmount if getValue == "Kr"
        mPeopleCardListItemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int iOweSum = 0;
                int xOwesSum = 0;
                int iOweAndXOwesBalance = 0;
                int displayNr = 0;

                int nrOfitems = 0;

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    for (DataSnapshot peopleCardItem: snapshot.getChildren()){

                        PeopleCardItem item = peopleCardItem.getValue(PeopleCardItem.class);

                        if (item.isiOwe() && item.getTypeOfValue().equalsIgnoreCase("kr")){
                            iOweSum = iOweSum + item.getAmount();
                        } else if (!item.isiOwe() && item.getTypeOfValue().equalsIgnoreCase("kr")) {
                            xOwesSum = xOwesSum + item.getAmount();
                        }

                        iOweAndXOwesBalance = iOweSum - xOwesSum;

                        // gets the positive value if the number is negative (so the display dosent show: "X owes you -500 kr")
                        if (iOweAndXOwesBalance < 0){
                            displayNr = -iOweAndXOwesBalance;
                        } else {
                            displayNr = iOweAndXOwesBalance;
                        }


                        // set mBalance text and round balance image
                        if (iOweAndXOwesBalance == 0){
                            mBalance.setText(getString(R.string.you_are_squared) + 0 + " " + getString(R.string.currency));
                            mRoundBalance.setImageResource(R.drawable.round_blue);
                        } else if (iOweAndXOwesBalance < 0) {
                            mBalance.setText(mPeopleCardFirstName + getString(R.string.owes_you) + displayNr + " " + getString(R.string.currency));
                            mRoundBalance.setImageResource(R.drawable.round_green);
                        } else if (iOweAndXOwesBalance > 0) {
                            mBalance.setText(getString(R.string.you_owe) + mPeopleCardFirstName + " " + displayNr + " " + getString(R.string.currency));
                            mRoundBalance.setImageResource(R.drawable.round_red);
                        } else {
                            mBalance.setText(getString(R.string.you_are_squared) + iOweAndXOwesBalance);
                            mRoundBalance.setImageResource(R.drawable.round_blue);
                        }

                        nrOfitems++;
                    }
                }
                // set balance and nr of items to the peopleCard at mPeopleCardRef
                mPeopleCard.setBalance(iOweAndXOwesBalance);
                mPeopleCard.setNumberOfItems(nrOfitems);
                mPeopleCardRef.setValue(mPeopleCard);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPeopleCardItemDialog(view);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Open dialog to add new PeopleCard
    public void showAddPeopleCardItemDialog(View view) {
        DialogFragment dialog = AddPeopleCardItemDialog.newInstance(mPeopleCardId, mPeopleCardFirstName);
        dialog.show(PeopleCardItemActivity.this.getFragmentManager(), "AddPeopleCardItemDialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
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
}
