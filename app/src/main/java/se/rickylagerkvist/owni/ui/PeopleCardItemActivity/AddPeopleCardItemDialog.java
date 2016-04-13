package se.rickylagerkvist.owni.ui.PeopleCardItemActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;

import com.firebase.client.Firebase;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.model.PeopleCardItem;
import se.rickylagerkvist.owni.utils.Constants;

/**
 * Created by Ricky on 2016-04-07.
 */
public class AddPeopleCardItemDialog extends DialogFragment {

    EditText mEditTextDescription, mEditTextAmount, mEditTextValue;
    RadioButton mRadioButtonIowe, mRadioButtonSomeoneOwesMe;

    private String id;

    public static AddPeopleCardItemDialog newInstance(String peopleCardId, String name) {
        AddPeopleCardItemDialog addListDialogFragment
                = new AddPeopleCardItemDialog();
        Bundle bundle = new Bundle();
        bundle.putString("id", peopleCardId);
        bundle.putString("name", name);
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
        View rootView = inflater.inflate(R.layout.add_people_card_item_dialog, null);

        Bundle bundle = this.getArguments();
        final String peopleCardId = bundle.getString("id");

        String name = bundle.getString("name");

        // Views
        mEditTextDescription = (EditText) rootView.findViewById(R.id.edit_description);
        mEditTextAmount = (EditText) rootView.findViewById(R.id.edit_amount);
        mEditTextValue = (EditText) rootView.findViewById(R.id.edit_value);
        mRadioButtonIowe = (RadioButton) rootView.findViewById(R.id.i_owe_radiobutton);
        mRadioButtonSomeoneOwesMe = (RadioButton) rootView.findViewById(R.id.someone_owes_me_radiobutton);

        // set text with correct name
        mRadioButtonIowe.setText("I Owe " + name);
        mRadioButtonSomeoneOwesMe.setText(name + " owes me");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(rootView)
                .setTitle("Create new PeopleCard item")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the dialog
                        AddPeopleCardItemDialog.this.getDialog().cancel();
                    }
                })
                        // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addPeopleCardItemToList(peopleCardId);
                    }
                });

        return builder.create();
    }

    private void addPeopleCardItemToList(String peopleCardId) {
        String userEnteredDescription = mEditTextDescription.getText().toString().trim();
        int userEnteredAmount = Integer.valueOf(mEditTextAmount.getText().toString().trim());
        String userEnteredValue = mEditTextValue.getText().toString().trim();


        if (mRadioButtonIowe.isChecked() && !userEnteredDescription.equals("") && userEnteredAmount >= 0 && !userEnteredValue.equals("")){

            Firebase listsRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS + "/"
                    + Constants.KEY_ENCODED_EMAIL).child(peopleCardId).child("iowe");
            Firebase newListRef = listsRef.push();

            //PeopleCard
            PeopleCardItem peopleCardItem = new PeopleCardItem(userEnteredDescription, userEnteredAmount, userEnteredValue, true);

            //Add to the list
            newListRef.setValue(peopleCardItem);

            // Close dialog
            AddPeopleCardItemDialog.this.getDialog().cancel();

        } else if (mRadioButtonSomeoneOwesMe.isChecked() && !userEnteredDescription.equals("") && userEnteredAmount >= 0 && !userEnteredValue.equals("")){

            Firebase listsRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS + "/" + Constants.KEY_ENCODED_EMAIL).child(peopleCardId).child("xowes");
            Firebase newListRef = listsRef.push();

            //PeopleCard
            PeopleCardItem peopleCardItem = new PeopleCardItem(userEnteredDescription, userEnteredAmount, userEnteredValue, false);

            //Add to the list
            newListRef.setValue(peopleCardItem);

            // Close dialog
            AddPeopleCardItemDialog.this.getDialog().cancel();

        } else  {
            AddPeopleCardItemDialog.this.getDialog().cancel();
        }

    }



}
