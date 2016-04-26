package se.rickylagerkvist.owni.ui.ActivityFragment;

import android.app.Activity;
import android.preference.PreferenceManager;
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

    String mNumberOfItems, mOwesMeBalance, mIOweBalance, mCurrency;

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
        mNumberOfItems = "" + activityCard.getNumberOfItems();
        textViewNrOfItems.setText(mNumberOfItems);

        // get mCurrency
        mCurrency = PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext()).getString("CURRENCY", "Select your currency");

        // set balance
        if (activityCard.getBalance() == 0) {
            textViewBalance.setText(mActivity.getString(R.string.you_are_squared));
        } else if (activityCard.getBalance() < 0) {
            mOwesMeBalance = "" + -activityCard.getBalance();
            textViewBalance.setText(mActivity.getString(R.string.other_people_owe_me_amount_currency, mOwesMeBalance, mCurrency));
        } else if (activityCard.getBalance() > 0) {
            mIOweBalance = "" + activityCard.getBalance();
            textViewBalance.setText(mActivity.getString(R.string.i_owe_other_people_amount_currency, mIOweBalance, mCurrency));
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