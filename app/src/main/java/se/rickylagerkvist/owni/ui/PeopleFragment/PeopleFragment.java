package se.rickylagerkvist.owni.ui.PeopleFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.client.Firebase;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.model.PeopleCard;
import se.rickylagerkvist.owni.utils.Constants;

/**

 */
public class PeopleFragment extends Fragment {

    private Firebase mFirebaseRef;
    private ListView mListView;
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
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_people, container, false);
        mListView = (ListView) rootView.findViewById(R.id.peopleCardList);

        // firebase ref
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL_PEOPLE + "/" + Constants.KEY_ENCODED_EMAIL);

        // set adapter for listView
        mPeopleCardAdapter = new PeopleCardAdapter(getActivity(), PeopleCard.class,
                R.layout.card_people, mFirebaseRef);
        mListView.setAdapter(mPeopleCardAdapter);

        return rootView;
    }

    // clean adapter
    @Override
    public void onDestroy() {
        super.onDestroy();
        mPeopleCardAdapter.cleanup();
    }

}
