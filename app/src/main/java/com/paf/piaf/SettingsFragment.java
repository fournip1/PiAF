package com.paf.piaf;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
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
        ListPreference numberOfQuestions = findPreference("nb_questions");

        // we first set the value from the user's value
        QCMPreference.setChecked(user.isQCM());
        numberOfQuestions.setValue(String.valueOf(user.getNbQuestions()));

        QCMPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                Log.i(SettingsFragment.class.getName(),"QCM chosen: "+ newValue);
                user.setQCM((boolean) newValue);
                userRuntimeExceptionDao.update(user);
                return true;
            }
        });

        numberOfQuestions.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                Log.i(SettingsFragment.class.getName(),"NB Questions: "+ newValue);
                user.setNbQuestions((int) Integer.parseInt((String) newValue));
                userRuntimeExceptionDao.update(user);
                return true;
            }
        });

    }

    @Override
    @MainThread
    @CallSuper
    public void onDestroy() {
        dBHelper.close();
        super.onDestroy();
    }
}