package se.rickylagerkvist.owni.ui.PeopleCardItem;

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
 * Created by Ricky on 2016-04-18.
 */
public class DeleteCardItemDialog extends DialogFragment {

    public static DeleteCardItemDialog newInstance(String peopleItemParentRef, String peopleItemIdRef, String iOweOfXOwe) {
        DeleteCardItemDialog deleteCardItemsDialog
                = new DeleteCardItemDialog();
        Bundle bundle = new Bundle();
        bundle.putString("idParent", peopleItemParentRef);
        bundle.putString("id", peopleItemIdRef);
        bundle.putString("bol", iOweOfXOwe);
        deleteCardItemsDialog.setArguments(bundle);
        return deleteCardItemsDialog;
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
        final String peopleItemParentRef = bundle.getString("idParent");
        final String peopleItemIdRef = bundle.getString("id");
        final String iOweOfXOwe = bundle.getString("bol");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle("Delete item")
                .setMessage(getActivity().getString(R.string.do_you_want_to_delete_item))
                .setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the dialog
                        DeleteCardItemDialog.this.getDialog().cancel();
                    }
                })
                // Add action buttons
                .setPositiveButton(getActivity().getString(R.string.delete_item), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        deleteCardItems(peopleItemParentRef, peopleItemIdRef, iOweOfXOwe);
                    }
                });

        return builder.create();
    }

    // Remove PeopleCardItem
    private void deleteCardItems(String peopleItemParentRef, String peopleItemIdRef, String iOweOfXOwe) {

        String userUid = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext()).getString("USERUID", "defaultStringIfNothingFound");

        Firebase PeopleCardItemRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS + "/"
                + userUid).child(peopleItemParentRef).child(iOweOfXOwe).child(peopleItemIdRef);
        PeopleCardItemRef.removeValue();

    }
}
