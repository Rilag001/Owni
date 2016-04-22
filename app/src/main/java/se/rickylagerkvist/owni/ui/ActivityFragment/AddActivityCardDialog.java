package se.rickylagerkvist.owni.ui.ActivityFragment;

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
import se.rickylagerkvist.owni.model.ActivityCard;
import se.rickylagerkvist.owni.utils.Constants;

/**
 * Created by Ricky on 2016-04-22.
 */
public class AddActivityCardDialog extends DialogFragment {

    EditText mEditTextListName;
    String mUserUid;

    public static AddActivityCardDialog newInstance() {
        AddActivityCardDialog addListDialogFragment
                = new AddActivityCardDialog();
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
        View rootView = inflater.inflate(R.layout.add_activitiescard_dialog, null);

        // Views
        mEditTextListName = (EditText) rootView.findViewById(R.id.edit_text_list_name);


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(rootView)
                .setTitle("Create new Activitiescard")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the dialog
                        AddActivityCardDialog.this.getDialog().cancel();
                    }
                })
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addActivitiesCardToList();
                    }
                });

        return builder.create();
    }

    private void addActivitiesCardToList() {
        String userEnteredName = mEditTextListName.getText().toString().trim();

        mUserUid = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext()).getString("USERUID", "defaultStringIfNothingFound");

        if (!userEnteredName.equals("")){
            Firebase listsRef = new Firebase(Constants.FIREBASE_URL_ACTIVITIES + "/" + mUserUid);
            Firebase newListRef = listsRef.push();

            //PeopleCard
            ActivityCard activityCard = new ActivityCard(userEnteredName);

            //Add to the list
            newListRef.setValue(activityCard);

            // Close dialog
            AddActivityCardDialog.this.getDialog().cancel();
        } else {
            AddActivityCardDialog.this.getDialog().cancel();
        }

    }
}
