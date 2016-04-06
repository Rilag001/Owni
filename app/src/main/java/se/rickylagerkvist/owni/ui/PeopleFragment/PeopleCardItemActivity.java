package se.rickylagerkvist.owni.ui.PeopleFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.Firebase;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.model.PeopleCard;
import se.rickylagerkvist.owni.utils.Constants;

public class PeopleCardItemActivity extends AppCompatActivity {

    private String mPeopleCardId;
    private PeopleCard mPeopleCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_card_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = this.getIntent();
        mPeopleCardId = intent.getStringExtra("PEOPLECARD_ITEM_ID");
        if (mPeopleCardId == null) {
            finish();
            return;
        }

        Firebase mPeopleCardRef = new Firebase(Constants.FIREBASE_URL_PEOPLE).child(mPeopleCardId);
        Firebase mPeopleCardListItemRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS).child(mPeopleCardId);


        Toast.makeText(PeopleCardItemActivity.this, mPeopleCardId, Toast.LENGTH_SHORT).show();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
