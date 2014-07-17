/*
 * This file is part of Prepay Credit for Android
 *
 * Copyright © 2014  Damien O'Reilly
 *
 * Prepay Credit for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Prepay Credit for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Prepay Credit for Android.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Report bugs or new features at: https://github.com/DamienOReilly/PrepayCredit
 * Contact the author at:          damienreilly@gmail.com
 */

package damo.three.ie.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import com.github.machinarius.preferencefragment.PreferenceFragment;
import damo.three.ie.R;
import damo.three.ie.net.ThreeHttpClient;
import damo.three.ie.util.UsageUtils;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * @author Damien O'Reilly
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        // Add functionality to the background-update checkbox to enable/disable alarms.
        Preference updateDailyPref = findPreference(getString(R.string.backgroundupdate));
        updateDailyPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (getPreferenceManager().getSharedPreferences().getBoolean(getString(R.string.backgroundupdate),
                        false)) {
                    UsageUtils.setupBackgroundUpdateAlarms(context, true);
                } else {
                    UsageUtils.setupBackgroundUpdateAlarms(context, false);
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        context = getActivity().getApplicationContext();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }


    /**
     * If the user changes credentials, clear the http cookies, as if the http cookies are still valid, they won't login
     * with the new credentials until the current cookies expire or are no longer valid.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ((key.equals("mobile") || key.equals("password"))) {
            try {
                ThreeHttpClient.getInstance(getActivity()).getHttpClient().getCookieStore().clear();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}