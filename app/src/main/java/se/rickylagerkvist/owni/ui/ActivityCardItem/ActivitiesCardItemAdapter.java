package se.rickylagerkvist.owni.ui.ActivityCardItem;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.model.ActivityCardItem;

/**
 * Created by Ricky on 2016-04-22.
 */
public class ActivitiesCardItemAdapter
        extends FirebaseListAdapter<ActivityCardItem> {

    public ActivitiesCardItemAdapter(Activity activity, Class<ActivityCardItem> modelClass, int modelLayout,
                                     Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View view, ActivityCardItem activityCardItem, int position) {

        TextView descriptionOfItem = (TextView) view.findViewById(R.id.activities_card_item_description);
        TextView amount = (TextView) view.findViewById(R.id.activities_card_item_value);
        TextView type = (TextView) view.findViewById(R.id.activities_card_item_type);
        TextView nameOfPerson = (TextView) view.findViewById(R.id.activities_card_item_name);

        // Set the var
        descriptionOfItem.setText(activityCardItem.getDescription());
        amount.setText("" + activityCardItem.getAmount());
        type.setText(activityCardItem.getTypeOfValue());
        nameOfPerson.setText(activityCardItem.getNameOfPerson());

    }
}
