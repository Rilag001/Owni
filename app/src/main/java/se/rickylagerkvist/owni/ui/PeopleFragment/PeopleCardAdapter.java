package se.rickylagerkvist.owni.ui.PeopleFragment;

import android.app.Activity;
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
        textViewNrOfItems.setText("" + peopleCard.getNumberOfItems());

        // set balance
        String mFirstName = peopleCard.getName().split(" ", 2)[0];
        if (peopleCard.getBalance() == 0){
            textViewBalance.setText(mActivity.getString(R.string.you_are_squared) + " 0 " + mActivity.getString(R.string.currency));
        } else if (peopleCard.getBalance() < 0){
            textViewBalance.setText(mFirstName + " " + mActivity.getString(R.string.owes_you) + " " + -peopleCard.getBalance() + " " + mActivity.getString(R.string.currency));
        } else if (peopleCard.getBalance() > 0) {
            textViewBalance.setText(mActivity.getString(R.string.you_owe) + " " + mFirstName + " " + peopleCard.getBalance() + " " + mActivity.getString(R.string.currency) );
        }

        // set round
        if (peopleCard.getBalance() == 0){
            round.setImageResource(R.drawable.round_blue);
        } else if (peopleCard.getBalance() > 0) {
            round.setImageResource(R.drawable.round_red);
        } else if (peopleCard.getBalance() < 0) {
            round.setImageResource(R.drawable.round_green);
        }
    }




}



