package com.learn.unittest;

import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link SharedPreferencesHelper} that mocks {@link SharedPreferences}.
 * Unsuccessful write of data on sharedPreference can occur due to various reasons such as
 * providing the wrong key, wrong context etc.
 *
 * Created by Safa Amin on 19/06/2019.
 */
@RunWith(MockitoJUnitRunner.class) //It instructs the IDE to initialize the Mockito library
public class SharedPreferencesHelperTest {

    private static final String TEST_NAME = "Test Name";
    private static final String TEST_EMAIL = "name@email.com";
    private static final Calendar TEST_DATE_OF_BIRTH = Calendar.getInstance();
    static {
        TEST_DATE_OF_BIRTH.set(1990, 1, 1);
    }
    private SharedPreferenceEntry sharedPreferenceEntry;
    private SharedPreferencesHelper mockNormalSharedPreferencesHelper;
    private SharedPreferencesHelper mockBrokenSharedPreferencesHelper;

    @Mock
    SharedPreferences mockNormalSharedPreferences;

    @Mock
    SharedPreferences mockBrokenSharedPreferences;

    @Mock
    SharedPreferences.Editor mockNormalEditor;

    @Mock
    SharedPreferences.Editor mockBrokenEditor;

    @Before
    public void initMocks() {
        // Create SharedPreferenceEntry to persist.
        sharedPreferenceEntry = new SharedPreferenceEntry(TEST_NAME,TEST_DATE_OF_BIRTH, TEST_EMAIL);
        // Create a mocked SharedPreferences.
        mockNormalSharedPreferencesHelper = createMockSharedPreference();
        // Create a mocked SharedPreferences that fails at saving data.
        mockBrokenSharedPreferencesHelper = createBrokenMockSharedPreference();
    }

    /**
     * Creates a mocked SharedPreferences.
     *
     * Mocking reading the SharedPreferences as if
     * mockNormalSharedPreferences was previously written correctly.
     */
    private SharedPreferencesHelper createMockSharedPreference() {

        when(mockNormalSharedPreferences.getString(eq(SharedPreferencesHelper.KEY_NAME), anyString()))
                .thenReturn(sharedPreferenceEntry.getName());

        when(mockNormalSharedPreferences.getLong(eq(SharedPreferencesHelper.KEY_DOB), anyLong()))
                .thenReturn(sharedPreferenceEntry.getDateOfBirth().getTimeInMillis());

        when(mockNormalSharedPreferences.getString(eq(SharedPreferencesHelper.KEY_EMAIL), anyString()))
                .thenReturn(sharedPreferenceEntry.getEmail());

        // Mocking a successful commit.
        when(mockNormalEditor.commit()).thenReturn(true);
        // Return the MockEditor when requesting it.
        when(mockNormalSharedPreferences.edit()).thenReturn(mockNormalEditor);

        return new SharedPreferencesHelper(mockNormalSharedPreferences);
    }

    /**
     * Creates a mocked SharedPreferences that fails when writing.
     */
    private SharedPreferencesHelper createBrokenMockSharedPreference() {
        // Mocking a commit that fails.
        when(mockBrokenEditor.commit()).thenReturn(false);

        // Return the broken MockEditor when requesting it.
        when(mockBrokenSharedPreferences.edit()).thenReturn(mockBrokenEditor);

        return new SharedPreferencesHelper(mockBrokenSharedPreferences);
    }

    @Test
    public void sharedPreferencesHelper_SaveAndReadPersonalInformation() {
        // Save the personal information to SharedPreferences

        boolean success = mockNormalSharedPreferencesHelper.savePersonalInfo(sharedPreferenceEntry);
        assertThat("Checking that SharedPreferenceEntry.save returns true", success, is(true));

        // Read personal information from SharedPreferences
        SharedPreferenceEntry savedSharedPreferenceEntry = mockNormalSharedPreferencesHelper.getPersonalInfo();

        // Make sure both written and retrieved personal information are equal.
        assertThat("Checking that SharedPreferenceEntry.name has been persisted and read correctly",
                sharedPreferenceEntry.getName(), is(equalTo(savedSharedPreferenceEntry.getName())));

        assertThat("Checking that SharedPreferenceEntry.dateOfBirth has been persisted and read",
                sharedPreferenceEntry.getDateOfBirth(), is(equalTo(savedSharedPreferenceEntry.getDateOfBirth())));

        assertThat("Checking that SharedPreferenceEntry.email has been persisted and read correctly",
                sharedPreferenceEntry.getEmail(),is(equalTo(savedSharedPreferenceEntry.getEmail())));
    }

    @Test
    public void sharedPreferencesHelper_SavePersonalInfoFailed() {
        // Read personal information from a broken SharedPreferencesHelper
        boolean success = mockBrokenSharedPreferencesHelper.savePersonalInfo(sharedPreferenceEntry);

        assertThat("Makes sure writing to a broken SharedPreferencesHelper returns false", success, is(false));
    }
}
