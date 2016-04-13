package se.rickylagerkvist.owni.ui.PeopleFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.model.PeopleCard;
import se.rickylagerkvist.owni.model.PeopleCardItem;
import se.rickylagerkvist.owni.ui.PeopleCardItemActivity.PeopleCardItemActivity;
import se.rickylagerkvist.owni.utils.Constants;

/**

 */
public class PeopleFragment extends Fragment {

    private Firebase mFirebaseRef;
    private ListView mListView;
    private PeopleCardAdapter mPeopleCardAdapter;

    private int nrOfItems;
    private int iOweAndXOwesBalance;


    public PeopleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_people, container, false);
        mListView = (ListView) rootView.findViewById(R.id.peopleCardList);

        // firebase ref
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL_PEOPLE + "/" + Constants.KEY_ENCODED_EMAIL);

        // set adapter for listView
        mPeopleCardAdapter = new PeopleCardAdapter(getActivity(), PeopleCard.class,
                R.layout.card_people, mFirebaseRef);
        mListView.setAdapter(mPeopleCardAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PeopleCard peopleCard = mPeopleCardAdapter.getItem(position);

                if (peopleCard != null) {
                    Intent intent = new Intent(getContext(), PeopleCardItemActivity.class);
                    String peopleCardId = mPeopleCardAdapter.getRef(position).getKey();
                    intent.putExtra("PEOPLECARD_ITEM_ID", peopleCardId);
                    startActivity(intent);
                }
            }
        });

        // set nr of item and balance for every Peoplecard by matching it to its corresponding PeopleCardItems

        mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    final PeopleCard peopleCard = postSnapshot.getValue(PeopleCard.class);
                    String PeopleCardItemKey = postSnapshot.getKey();

                    Firebase mPeopleCardListItemRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS
                            + "/" + Constants.KEY_ENCODED_EMAIL).child(PeopleCardItemKey);

                    mPeopleCardListItemRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            int iOweSum = 0;
                            int xOwesSum = 0;
                            iOweAndXOwesBalance = 0;
                            int nrOfItems = 0;

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                for (DataSnapshot peopleCardItem : snapshot.getChildren()) {

                                    PeopleCardItem item = peopleCardItem.getValue(PeopleCardItem.class);

                                    if (item.isiOwe() && item.getTypeOfValue().equalsIgnoreCase("kr")) {
                                        iOweSum = iOweSum + item.getAmount();
                                    } else if (!item.isiOwe() && item.getTypeOfValue().equalsIgnoreCase("kr")) {
                                        xOwesSum = xOwesSum + item.getAmount();
                                    }

                                    // balance
                                    iOweAndXOwesBalance = iOweSum - xOwesSum;

                                    // nr of items
                                    nrOfItems = nrOfItems + 1;

                                    peopleCard.setNumberOfItems(nrOfItems);
                                    peopleCard.setBalance(iOweAndXOwesBalance);
                                    mPeopleCardAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return rootView;
    }

    // clean adapter
    @Override
    public void onDestroy() {
        super.onDestroy();
        mPeopleCardAdapter.cleanup();
    }

}
