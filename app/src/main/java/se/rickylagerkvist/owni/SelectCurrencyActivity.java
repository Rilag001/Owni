package se.rickylagerkvist.owni;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import se.rickylagerkvist.owni.ui.MainActivity;

public class SelectCurrencyActivity extends AppCompatActivity {
    Spinner mSpinner;
    ArrayAdapter<CharSequence> mAdapter;
    boolean mSpinnerSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_curreny);

        // set spinner adapter
        mSpinner = (Spinner) findViewById(R.id.currency_spinner);
        mAdapter = ArrayAdapter.createFromResource(this, R.array.currency,
                R.layout.currency_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);

        mSpinner.setDrawingCacheBackgroundColor(getResources().getColor(android.R.color.white));

        // set OnitemSelected
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // save selected string to getDefaultSharedPreferences
                String mCurrency = parent.getItemAtPosition(position).toString();
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("CURRENCY", mCurrency).apply();

                // set mSpinnerSelected to true, if != parent.getItemAtPosition(0)
                mSpinnerSelected = mCurrency != parent.getItemAtPosition(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // open MainActivity if the user has a chosen currency
    public void openMainActivity(View view) {
        if (mSpinnerSelected){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Snackbar snackbar = Snackbar
                    .make(view, "Please select you currency", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }
}
