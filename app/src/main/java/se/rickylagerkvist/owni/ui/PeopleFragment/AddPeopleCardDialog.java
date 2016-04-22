package se.rickylagerkvist.owni.ui.PeopleFragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.firebase.client.Firebase;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.model.PeopleCard;
import se.rickylagerkvist.owni.utils.Constants;

/**
 * Created by Ricky on 2016-04-05.
 */
public class AddPeopleCardDialog extends DialogFragment {

    EditText mEditTextListName;
    String mUserUid;

    public static AddPeopleCardDialog newInstance() {
        AddPeopleCardDialog addListDialogFragment
                = new AddPeopleCardDialog();
        Bundle bundle = new Bundle();
        addListDialogFragment.setArguments(bundle);
        return addListDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Open the keyboard automatically when the dialog fragment is opened
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.add_peoplecard_dialog, null);

        // Views
        mEditTextListName = (EditText) rootView.findViewById(R.id.edit_text_list_name);


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(rootView)
                .setTitle(getActivity().getString(R.string.new_peoplecard))
                .setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the dialog
                        AddPeopleCardDialog.this.getDialog().cancel();
                    }
                })
                // Add action buttons
                .setPositiveButton(getActivity().getString(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addPeopleCardToList();
                    }
                });

        return builder.create();
    }

    private void addPeopleCardToList() {
        String userEnteredName = mEditTextListName.getText().toString().trim();

        mUserUid = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext()).getString("USERUID", "defaultStringIfNothingFound");

        if (!userEnteredName.equals("")) {
            Firebase listsRef = new Firebase(Constants.FIREBASE_URL_PEOPLE + "/" + mUserUid);
            Firebase newListRef = listsRef.push();

            //PeopleCard
            PeopleCard peopleCard = new PeopleCard(userEnteredName);

            //Add to the list
            newListRef.setValue(peopleCard);

            // Close dialog
            AddPeopleCardDialog.this.getDialog().cancel();
        } else {
            AddPeopleCardDialog.this.getDialog().cancel();
        }

    }
}
