package se.rickylagerkvist.owni.ui.PeopleCardItemActivity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        Firebase mPeopleCardRef = new Firebase(Constants.FIREBASE_URL_PEOPLE + "/"
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
                mPeopleCardFirstName = peopleCard.getName().split(" ", 2)[0];
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

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    for (DataSnapshot peopleCardItem: snapshot.getChildren()){

                        PeopleCardItem item = peopleCardItem.getValue(PeopleCardItem.class);

                        if (item.isiOwe() && item.getTypeOfValue().equalsIgnoreCase("kr")){
                            iOweSum = iOweSum + item.getAmount();
                        } else if (!item.isiOwe() && item.getTypeOfValue().equalsIgnoreCase("kr")) {
                            xOwesSum = xOwesSum + item.getAmount();
                        }

                        iOweAndXOwesBalance = iOweSum - xOwesSum;

                        // set mBalance text and round balance image
                        if (iOweAndXOwesBalance == 0){
                            mBalance.setText(getString(R.string.you_are_squared) + 0 + " " + getString(R.string.currency));
                            mRoundBalance.setImageResource(R.drawable.round_blue);
                        } else if (iOweAndXOwesBalance < 0) {
                            mBalance.setText(mPeopleCardFirstName + getString(R.string.owes_you) + iOweAndXOwesBalance + " " + getString(R.string.currency));
                            mRoundBalance.setImageResource(R.drawable.round_green);
                        } else if (iOweAndXOwesBalance > 0) {
                            mBalance.setText(getString(R.string.you_owe) + mPeopleCardFirstName + " " + iOweAndXOwesBalance + " " + getString(R.string.currency));
                            mRoundBalance.setImageResource(R.drawable.round_red);
                        } else {
                            mBalance.setText(getString(R.string.you_are_squared) + iOweAndXOwesBalance);
                            mRoundBalance.setImageResource(R.drawable.round_blue);
                        }
                    }
                }


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



}
