package se.rickylagerkvist.owni.ui.PeopleFragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.Firebase;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.model.PeopleCard;
import se.rickylagerkvist.owni.ui.PeopleCardItem.PeopleCardItemActivity;
import se.rickylagerkvist.owni.utils.Constants;

public class PeopleFragment extends Fragment {

    private PeopleCardAdapter mPeopleCardAdapter;


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

        String userUid = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("USERUID", "defaultStringIfNothingFound");

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_people, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.peopleCardList);

        // firebase ref
        Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL_PEOPLE + "/" + userUid);

        // set adapter for listView
        mPeopleCardAdapter = new PeopleCardAdapter(getActivity(), PeopleCard.class,
                R.layout.card_fragment, firebaseRef);
        listView.setAdapter(mPeopleCardAdapter);

        // open PeopleCardItemActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        return rootView;
    }


    // clean adapter
    @Override
    public void onDestroy() {
        super.onDestroy();
        mPeopleCardAdapter.cleanup();
    }

}
