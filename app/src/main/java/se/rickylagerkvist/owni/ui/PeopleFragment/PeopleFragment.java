package se.rickylagerkvist.owni.ui.PeopleFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.client.Firebase;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.utils.Constants;

/**

 */
public class PeopleFragment extends Fragment {

    private Firebase mFirebaseRef;
    private ListView mListView;


    public PeopleFragment() {
        // Required empty public constructor
    }


    public static PeopleFragment newInstance() {
        PeopleFragment fragment = new PeopleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL_USERS + "/" + Constants.KEY_ENCODED_EMAIL);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
