package se.rickylagerkvist.owni.ui.PeopleCardItemActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.firebase.client.Firebase;

import se.rickylagerkvist.owni.ui.MainActivity;
import se.rickylagerkvist.owni.utils.Constants;

/**
 * Created by Ricky on 2016-04-13.
 */
public class DeleteCardAndItemsDialog extends DialogFragment {

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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle bundle = this.getArguments();
        final String peopleCardAndItemId = bundle.getString("id");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle("Delete Card")
                .setMessage("Are you sure you want to delete this card and all its Items?")
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

    private void deleteCardAndItems(String peopleCardAndItemId) {

        Firebase peopleCardRef = new Firebase(Constants.FIREBASE_URL_PEOPLE + "/"
                + Constants.KEY_ENCODED_EMAIL).child(peopleCardAndItemId);
        Firebase PeopleCardItemRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS + "/"
                + Constants.KEY_ENCODED_EMAIL).child(peopleCardAndItemId);

        peopleCardRef.removeValue();
        PeopleCardItemRef.removeValue();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);

    }

}
