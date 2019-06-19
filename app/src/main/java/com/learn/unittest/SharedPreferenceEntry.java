package com.learn.unittest;

import java.util.Calendar;

/**
 * A Model class containing personal information that will be saved to SharedPreferences.
 *
 * Created by Safa Amin on 19/06/2019.
 */
public class SharedPreferenceEntry {

    private final String mName;
    private final Calendar mDateOfBirth;
    private final String mEmail;

    public SharedPreferenceEntry(String name, Calendar dateOfBirth, String email) {
        mName = name;
        mDateOfBirth = dateOfBirth;
        mEmail = email;
    }

    public String getName() {
        return mName;
    }

    public Calendar getDateOfBirth() {
        return mDateOfBirth;
    }

    public String getEmail() {
        return mEmail;
    }
}
