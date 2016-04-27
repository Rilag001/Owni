package se.rickylagerkvist.owni.ui.PeopleCardItem;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.model.PeopleCardItem;

/**
 * Created by Ricky on 2016-04-07.
 */
public class PeopleCardItemAdapter
        extends FirebaseListAdapter<PeopleCardItem> {

    public PeopleCardItemAdapter(Activity activity, Class<PeopleCardItem> modelClass, int modelLayout,
                                 Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View view, PeopleCardItem peopleCardItem, int position) {

        TextView descriptionOfItem = (TextView) view.findViewById(R.id.people_card_item_description);
        TextView amount = (TextView) view.findViewById(R.id.people_card_item_value);
        TextView type = (TextView) view.findViewById(R.id.people_card_item_type);


        // Set the var
        descriptionOfItem.setText(peopleCardItem.getDescription());
        amount.setText("" + peopleCardItem.getAmount());
        type.setText(peopleCardItem.getTypeOfValue());

    }


}
