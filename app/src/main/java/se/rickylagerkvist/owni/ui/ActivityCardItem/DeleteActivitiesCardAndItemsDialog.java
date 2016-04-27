package se.rickylagerkvist.owni.ui.ActivityCardItem;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.firebase.client.Firebase;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.utils.Constants;

/**
 * Created by Ricky on 2016-04-22.
 */
public class DeleteActivitiesCardAndItemsDialog extends DialogFragment {

    public static DeleteActivitiesCardAndItemsDialog newInstance(String peopleCardAndItemRef) {
        DeleteActivitiesCardAndItemsDialog deleteActivitiesCardAndItemsDialog
                = new DeleteActivitiesCardAndItemsDialog();
        Bundle bundle = new Bundle();
        bundle.putString("id", peopleCardAndItemRef);
        deleteActivitiesCardAndItemsDialog.setArguments(bundle);
        return deleteActivitiesCardAndItemsDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle bundle = this.getArguments();
        final String peopleCardAndItemId = bundle.getString("id");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle(getActivity().getString(R.string.delete_card))
                .setMessage(getActivity().getString(R.string.delete_card_and_items))
                .setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the dialog
                        DeleteActivitiesCardAndItemsDialog.this.getDialog().cancel();
                    }
                })
                // Add action buttons
                .setPositiveButton(getActivity().getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        deleteCardAndItems(peopleCardAndItemId);
                    }
                });

        return builder.create();
    }

    // Remove ActivitiesCard and ActivitiesCardItems
    private void deleteCardAndItems(String peopleCardAndItemId) {

        String userUid = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext()).getString("USERUID", "defaultStringIfNothingFound");

        Firebase activitiesCardRef = new Firebase(Constants.FIREBASE_URL_ACTIVITIES + "/"
                + userUid).child(peopleCardAndItemId);
        Firebase activitiesItemRef = new Firebase(Constants.FIREBASE_URL_ACTIVITIES_ITEMS + "/"
                + userUid).child(peopleCardAndItemId);

        activitiesCardRef.removeValue();
        activitiesItemRef.removeValue();

        // returns to this Activity's parent Activity, in this case MainActivity
        getActivity().finish();
    }

}

