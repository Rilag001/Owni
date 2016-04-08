package se.rickylagerkvist.owni.ui.PeopleCardItemActivity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

    private String mPeopleCardId;
    private String mPeopleCardName;
    private PeopleCard mPeopleCard;
    private FloatingActionButton fab;

    private ListView mIOweListView, mXOwesListView;
    private TextView mIoweTitle, mXOwesTitle;

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

        Intent intent = this.getIntent();
        mPeopleCardId = intent.getStringExtra("PEOPLECARD_ITEM_ID");
        if (mPeopleCardId == null) {
            finish();
            return;
        }

        // PeopleCard
        Firebase mPeopleCardRef = new Firebase(Constants.FIREBASE_URL_PEOPLE + "/" + Constants.KEY_ENCODED_EMAIL).child(mPeopleCardId);
        // PeopleCard items
        Firebase mPeopleCardListItemIOweRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS + "/" + Constants.KEY_ENCODED_EMAIL).child(mPeopleCardId).child("iowe");
        Firebase mPeopleCardListItemXOwesRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS + "/" + Constants.KEY_ENCODED_EMAIL).child(mPeopleCardId).child("xowes");


        // I owe
        mIoweXAdapter = new PeopleCardItemAdapter(PeopleCardItemActivity.this, PeopleCardItem.class,
                R.layout.card_people_item, mPeopleCardListItemIOweRef);
        mIOweListView.setAdapter(mIoweXAdapter);

        // Other owe
        mXowesMeAdapter = new PeopleCardItemAdapter(PeopleCardItemActivity.this, PeopleCardItem.class,
                R.layout.card_people_item, mPeopleCardListItemXOwesRef);
        mXOwesListView.setAdapter(mXowesMeAdapter);


        // set toolbar titel and color
        mPeopleCardRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get Peoplecard
                PeopleCard peopleCard = dataSnapshot.getValue(PeopleCard.class);
                // set mPeopleCard to peoplecard for ev later use
                mPeopleCard = peopleCard;
                // first name of peoplecard
                mPeopleCardName = peopleCard.getName().split (" ", 2) [0];
                // set toolbar titel to PeopleCards name
                toolbar.setTitle(peopleCard.getName());




                /* and color
                int balanceColor = 0;
                if (peopleCard.getBalance() == 0) {
                    balanceColor = getResources().getColor(R.color.blueColor);
                } else if (peopleCard.getBalance() > 0) {
                    balanceColor = getResources().getColor(R.color.colorAccent);
                } else if (peopleCard.getBalance() < 0) {
                    balanceColor = getResources().getColor(R.color.colorPrimary);
                }
                toolbar.setBackgroundColor(balanceColor);

                if (Build.VERSION.SDK_INT >= 21) {
                    getWindow().setStatusBarColor(balanceColor);
                }*/

                // set title to include name
                mIoweTitle.setText(getResources().getString(R.string.i_owe_person) + " " +  mPeopleCardName);
                mXOwesTitle.setText(mPeopleCardName + " " + getResources().getString(R.string.person_owes_me));
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
        DialogFragment dialog = AddPeopleCardItemDialog.newInstance(mPeopleCardId, mPeopleCardName);
        dialog.show(PeopleCardItemActivity.this.getFragmentManager(), "AddPeopleCardItemDialog");
    }

}
