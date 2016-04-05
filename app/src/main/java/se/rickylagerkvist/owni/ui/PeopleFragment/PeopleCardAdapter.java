package se.rickylagerkvist.owni.ui.PeopleFragment;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.model.PeopleCard;

/**
 * Created by Ricky on 2016-04-05.
 */
public class PeopleCardAdapter
        extends FirebaseListAdapter<PeopleCard> {

    public PeopleCardAdapter(Activity activity, Class<PeopleCard> modelClass, int modelLayout,
                             Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View view, PeopleCard list) {

        TextView textViewName = (TextView) view.findViewById(R.id.i_or_you_owe_person_x);


        /* Set the var */
        textViewName.setText(list.getName());

    }
}
