package se.rickylagerkvist.owni.ui.PeopleCardItemActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.firebase.client.Firebase;

import se.rickylagerkvist.owni.utils.Constants;

/**
 * Created by Ricky on 2016-04-13.
 */
public class DeleteCardAndItemsDialog extends DialogFragment {

    private String mUserUid;

    public static DeleteCardAndItemsDialog newInstance(String peopleCardAndItemRef) {
        DeleteCardAndItemsDialog deleteCardAndItemsDialog
                = new DeleteCardAndItemsDialog();
        Bundle bundle = new Bundle();
        bundle.putString("id", peopleCardAndItemRef);
        deleteCardAndItemsDialog.setArguments(bundle);
        return deleteCardAndItemsDialog;
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
        builder.setTitle("Delete Card")
                .setMessage("Are you sure you want to delete this card and all its items?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the dialog
                        DeleteCardAndItemsDialog.this.getDialog().cancel();
                    }
                })
                        // Add action buttons
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        deleteCardAndItems(peopleCardAndItemId);
                    }
                });

        return builder.create();
    }

    // Remove PeopleCard and PeopleCardItems
    private void deleteCardAndItems(String peopleCardAndItemId) {

        mUserUid = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext()).getString("USERUID", "defaultStringIfNothingFound");

        Firebase peopleCardRef = new Firebase(Constants.FIREBASE_URL_PEOPLE + "/"
                + mUserUid).child(peopleCardAndItemId);
        Firebase PeopleCardItemRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS + "/"
                + mUserUid).child(peopleCardAndItemId);

        peopleCardRef.removeValue();
        PeopleCardItemRef.removeValue();

        // returns to this Activity's parent Activity, in this case MainActivity
        getActivity().finish();
    }

}
