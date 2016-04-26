package se.rickylagerkvist.owni.ui.ActivityFragment;

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
import se.rickylagerkvist.owni.model.ActivityCard;
import se.rickylagerkvist.owni.ui.ActivityCardItemActivity.ActivitiesCardItemActivity;
import se.rickylagerkvist.owni.utils.Constants;

public class ActivitiesFragment extends Fragment {

    private ActivityCardAdapter mActivityCardAdapter;

    public ActivitiesFragment() {
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
        final View rootView = inflater.inflate(R.layout.fragment_activities, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.activitiesCardList);

        // firebase ref
        Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL_ACTIVITIES + "/" + userUid);

        // set adapter for listView
        mActivityCardAdapter = new ActivityCardAdapter(getActivity(), ActivityCard.class,
                R.layout.card_fragment, firebaseRef);
        listView.setAdapter(mActivityCardAdapter);

        // open PeopleCardItemActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActivityCard activityCard = mActivityCardAdapter.getItem(position);

                if (activityCard != null) {
                    Intent intent = new Intent(getContext(), ActivitiesCardItemActivity.class);
                    String activityCardId = mActivityCardAdapter.getRef(position).getKey();
                    intent.putExtra("ACTIVITIESCARD_ITEM_ID", activityCardId);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivityCardAdapter.cleanup();
    }
}
