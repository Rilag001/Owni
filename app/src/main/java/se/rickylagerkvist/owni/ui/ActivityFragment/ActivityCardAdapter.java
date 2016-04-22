package se.rickylagerkvist.owni.ui.ActivityFragment;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Query;
import com.firebase.ui.FirebaseListAdapter;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.model.ActivityCard;

/**
 * Created by Ricky on 2016-04-22.
 */
public class ActivityCardAdapter extends
        FirebaseListAdapter<ActivityCard> {

    public ActivityCardAdapter(Activity activity, Class<ActivityCard> modelClass, int modelLayout,
                               Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View view, ActivityCard activityCard) {

        TextView textViewName = (TextView) view.findViewById(R.id.name);
        TextView textViewNrOfItems = (TextView) view.findViewById(R.id.nrOfItems);
        TextView textViewBalance = (TextView) view.findViewById(R.id.balance);
        ImageView round = (ImageView) view.findViewById(R.id.round);

        // Set name
        textViewName.setText(activityCard.getNameOfActivity());

        // set nr of items
        textViewNrOfItems.setText("" + activityCard.getNumberOfItems());

        // set balance
        if (activityCard.getBalance() == 0) {
            textViewBalance.setText(mActivity.getString(R.string.balance_is_0) + "" + mActivity.getString(R.string.currency));
        } else if (activityCard.getBalance() < 0) {
            textViewBalance.setText("Other people" + " " + mActivity.getString(R.string.owes_you) + " " + -activityCard.getBalance() + " " + mActivity.getString(R.string.currency));
        } else if (activityCard.getBalance() > 0) {
            textViewBalance.setText(mActivity.getString(R.string.you_owe) + " other people " + activityCard.getBalance() + " " + mActivity.getString(R.string.currency));
        }

        // set mRound
        if (activityCard.getBalance() == 0) {
            round.setImageResource(R.drawable.round_blue);
        } else if (activityCard.getBalance() > 0) {
            round.setImageResource(R.drawable.round_red);
        } else if (activityCard.getBalance() < 0) {
            round.setImageResource(R.drawable.round_green);
        }
    }
}