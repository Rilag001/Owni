package se.rickylagerkvist.owni.ui.PeopleFragment;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
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

    String mNumberOfItems, mOwesMeBalance, mIOweBalance, mCurrency;

    public PeopleCardAdapter(Activity activity, Class<PeopleCard> modelClass, int modelLayout,
                             Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    @Override
    protected void populateView(View view, PeopleCard peopleCard) {

        TextView textViewName = (TextView) view.findViewById(R.id.name);
        TextView textViewNrOfItems = (TextView) view.findViewById(R.id.nrOfItems);
        TextView textViewBalance = (TextView) view.findViewById(R.id.balance);
        ImageView round = (ImageView) view.findViewById(R.id.round);

        // Set name
        textViewName.setText(peopleCard.getName());

        // set nr of items
        mNumberOfItems = "" + peopleCard.getNumberOfItems();
        textViewNrOfItems.setText(mNumberOfItems);

        // get mCurrency
        mCurrency = PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext()).getString("CURRENCY", "Select your currency");

        // set balance
        String mFirstName = peopleCard.getName().split(" ", 2)[0];
        if (peopleCard.getBalance() == 0) {
            textViewBalance.setText(mActivity.getString(R.string.you_are_squared));
        } else if (peopleCard.getBalance() < 0) {
            mOwesMeBalance = "" + -peopleCard.getBalance();
            textViewBalance.setText(mActivity.getString(R.string.person_owes_me_amount_currency, mFirstName, mOwesMeBalance, mCurrency));
        } else if (peopleCard.getBalance() > 0) {
            mIOweBalance = "" + peopleCard.getBalance();
            textViewBalance.setText(mActivity.getString(R.string.i_owe_person_amount_currency, mFirstName, mIOweBalance, mCurrency));
        }

        // set mRound
        if (peopleCard.getBalance() == 0) {
            round.setImageResource(R.drawable.round_blue);
        } else if (peopleCard.getBalance() > 0) {
            round.setImageResource(R.drawable.round_red);
        } else if (peopleCard.getBalance() < 0) {
            round.setImageResource(R.drawable.round_green);
        }
    }
}

