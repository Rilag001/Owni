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
    protected void populateView(View view, PeopleCard list) {

        TextView textViewName = (TextView) view.findViewById(R.id.name);
        TextView textViewNrOfItems = (TextView) view.findViewById(R.id.nrOfItems);
        TextView textViewBalance = (TextView) view.findViewById(R.id.balance);
        ImageView round = (ImageView) view.findViewById(R.id.round);


        // Set the var
        textViewName.setText(list.getName());
        textViewNrOfItems.setText("" + list.getNumberOfItems());
        textViewBalance.setText("" + list.getBalance());

        if (list.getBalance() == 0){
            round.setImageResource(R.drawable.round_blue);
        } else if (list.getBalance() > 0) {
            round.setImageResource(R.drawable.round_red);
        } else if (list.getBalance() < 0) {
            round.setImageResource(R.drawable.round_green);
        }
    }
}
