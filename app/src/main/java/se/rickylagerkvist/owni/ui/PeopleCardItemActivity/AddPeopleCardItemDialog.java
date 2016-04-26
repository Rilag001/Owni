package se.rickylagerkvist.owni.ui.PeopleCardItemActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.firebase.client.Firebase;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.model.PeopleCardItem;
import se.rickylagerkvist.owni.utils.Constants;

/**
 * Created by Ricky on 2016-04-07.
 */
public class AddPeopleCardItemDialog extends DialogFragment {

    EditText mEditTextDescription, mEditTextAmount;
    RadioButton mRadioButtonIowe, mRadioButtonSomeoneOwesMe;
    String userEnteredValue;

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

    @NonNull
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

        // get currency
        String mCurrency = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("CURRENCY", "Select your currency");
        // set array for Spinner
        String[] currencyOrItem = {mCurrency, "item"};

        // Radiobuttons
        mRadioButtonIowe = (RadioButton) rootView.findViewById(R.id.i_owe_radiobutton);
        mRadioButtonSomeoneOwesMe = (RadioButton) rootView.findViewById(R.id.someone_owes_me_radiobutton);
        mRadioButtonIowe.setText(getActivity().getString(R.string.i_owe_person, name));
        mRadioButtonSomeoneOwesMe.setText(getActivity().getString(R.string.person_owes_me, name));

        // Edit text
        mEditTextDescription = (EditText) rootView.findViewById(R.id.edit_description);
        mEditTextAmount = (EditText) rootView.findViewById(R.id.edit_amount);

        // Spinner
        Spinner mSpinner = (Spinner) rootView.findViewById(R.id.spinner_add_peoplecard_item);
        // set spinner adapter
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, currencyOrItem);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
        // set OnitemSelected
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userEnteredValue = "" + parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(rootView)
                .setTitle(getActivity().getString(R.string.new_peoplecard_item))
                .setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the dialog
                        AddPeopleCardItemDialog.this.getDialog().cancel();
                    }
                })
                        // Add action buttons
                .setPositiveButton(getActivity().getString(R.string.add), new DialogInterface.OnClickListener() {
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
        String userUid = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext()).getString("USERUID", "defaultStringIfNothingFound");

        if (mRadioButtonIowe.isChecked() && !userEnteredDescription.equals("") && userEnteredAmount >= 0 && !userEnteredValue.equals("")){

            Firebase listsRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS + "/" + userUid).child(peopleCardId).child("iowe");
            Firebase newListRef = listsRef.push();

            //PeopleCard
            PeopleCardItem peopleCardItem = new PeopleCardItem(userEnteredDescription, userEnteredAmount, userEnteredValue, true);

            //Add to the list
            newListRef.setValue(peopleCardItem);

            // Close dialog
            AddPeopleCardItemDialog.this.getDialog().cancel();

        } else if (mRadioButtonSomeoneOwesMe.isChecked() && !userEnteredDescription.equals("") && userEnteredAmount >= 0 && !userEnteredValue.equals("")){

            Firebase listsRef = new Firebase(Constants.FIREBASE_URL_PEOPLE_ITEMS + "/" + userUid).child(peopleCardId).child("xowes");
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
