package com.paf.piaf;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.j256.ormlite.dao.RuntimeExceptionDao;

public class SettingsFragment extends PreferenceFragmentCompat {
    private DatabaseHelper dBHelper;
    private User user;
    private RuntimeExceptionDao<User, Integer> userRuntimeExceptionDao;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // opening database and getting the user
        dBHelper = new DatabaseHelper(getActivity());
        userRuntimeExceptionDao = dBHelper.getUserRuntimeDao();
        user = userRuntimeExceptionDao.queryForAll().get(0);

        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        SwitchPreferenceCompat QCMPreference = findPreference("qcm_mode");
        SwitchPreferenceCompat warningPreference = findPreference("warning");

        ListPreference numberOfQuestions = findPreference("nb_questions");
        ListPreference numberOfChoices = findPreference("nb_choices");

        // we first set the value from the user's value
        QCMPreference.setChecked(user.isQCM());
        warningPreference.setChecked(user.isWarning());
        numberOfQuestions.setValue(String.valueOf(user.getNbQuestions()));
        numberOfChoices.setValue(String.valueOf(user.getNbChoices()));

        QCMPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                // Log.i(SettingsFragment.class.getName(),"QCM chosen: "+ newValue);
                user.setQCM((boolean) newValue);
                userRuntimeExceptionDao.update(user);
                return true;
            }
        });

        warningPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                // Log.i(SettingsFragment.class.getName(),"QCM chosen: "+ newValue);
                user.setWarning((boolean) newValue);
                userRuntimeExceptionDao.update(user);
                return true;
            }
        });

        numberOfQuestions.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                // Log.i(SettingsFragment.class.getName(),"NB Questions: "+ newValue);
                user.setNbQuestions(Integer.parseInt((String) newValue));
                userRuntimeExceptionDao.update(user);
                return true;
            }
        });


        numberOfChoices.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                // Log.i(SettingsFragment.class.getName(),"NB Choices: "+ newValue);
                user.setNbChoices(Integer.parseInt((String) newValue));
                userRuntimeExceptionDao.update(user);
                return true;
            }
        });
    }

    @Override
    @MainThread
    @CallSuper
    public void onDestroy() {
        if (dBHelper!=null) {
            dBHelper.close();
        }
        super.onDestroy();
    }
}