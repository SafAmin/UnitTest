package com.learn.unittest;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

/**
 * An Activity that represents an input form page where the user can provide his name, date
 * of birth and email address. The personal information can be saved to {@link SharedPreferences}
 * by clicking save button.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG =  MainActivity.class.getName();
    private EditText etName, etEmail;
    private DatePicker dpDateOfBirth;
    private EmailValidator mEmailValidator;
    private Button btnSave, btnRetrieve;
    // The helper that manages writing to SharedPreferences.
    private SharedPreferencesHelper mSharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    /**
     * Invalidate the views, Setup field validators and Instantiate a SharedPreferencesHelper
     * then call{@code populateUi} method to fill input fields from data retrieved
     * from the SharedPreferences.
     */
    private void initViews() {
        etName = findViewById(R.id.et_name);
        dpDateOfBirth = findViewById(R.id.dp_birthday);
        etEmail =  findViewById(R.id.et_email);
        btnSave = findViewById(R.id.btn_save);
        btnRetrieve =findViewById(R.id.btn_retrieve);


        mEmailValidator = new EmailValidator();
        etEmail.addTextChangedListener(mEmailValidator);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferencesHelper = new SharedPreferencesHelper(sharedPreferences);

        populateUi();
        setActions();
    }

    /**
     * Initialize all fields from the personal info saved in the SharedPreferences.
     */
    private void populateUi() {
        SharedPreferenceEntry sharedPreferenceEntry;
        sharedPreferenceEntry = mSharedPreferencesHelper.getPersonalInfo();

        etName.setText(sharedPreferenceEntry.getName());
        Calendar dateOfBirth = sharedPreferenceEntry.getDateOfBirth();
        dpDateOfBirth.init(dateOfBirth.get(Calendar.YEAR), dateOfBirth.get(Calendar.MONTH),
                dateOfBirth.get(Calendar.DAY_OF_MONTH), null);
        etEmail.setText(sharedPreferenceEntry.getEmail());
    }

    private void setActions() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveClick();
            }
        });

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRevertClick();
            }
        });
    }

    /**
     * Called when the "Save" button is clicked.
     */
    public void onSaveClick() {
        // Don't save if the fields do not validate.
        if (!mEmailValidator.isValid()) {
            etEmail.setError("Invalid email");
            Log.w(TAG, "Not saving personal information: Invalid email");

            return;
        }
        // Get the text from the input fields.
        String name = etName.getText().toString();
        Calendar dateOfBirth = Calendar.getInstance();
        dateOfBirth.set(dpDateOfBirth.getYear(), dpDateOfBirth.getMonth(), dpDateOfBirth.getDayOfMonth());
        String email = etEmail.getText().toString();

        // Create a Setting model class to persist.
        SharedPreferenceEntry sharedPreferenceEntry =
                new SharedPreferenceEntry(name, dateOfBirth, email);

        // Persist the personal information.
        boolean isSuccess = mSharedPreferencesHelper.savePersonalInfo(sharedPreferenceEntry);
        if (isSuccess) {
            Toast.makeText(this, "Personal information saved", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Personal information saved");
        } else {
            Log.e(TAG, "Failed to write personal information to SharedPreferences");
        }
    }

    /**
     * Called when the "Revert" button is clicked.
     */
    public void onRevertClick() {
        populateUi();
        Toast.makeText(this, "Personal information reverted", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Personal information reverted");
    }
}
