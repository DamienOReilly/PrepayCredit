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

package damo.three.ie.prepay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Executes the update service, ensuring the device doesn't fall back asleep before the service gets a chance to run.
 *
 * @author Damien O'Reilly
 */
public class UpdateReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        // check if we have background update enabled and user has credentials set
        if (sharedPrefs.getBoolean("backgroundupdate", true) && !(sharedPrefs.getString("mobile", "").equals("")
                && sharedPrefs.getString("password", "").equals(""))) {

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            // only when connected or while connecting...
            if (netInfo != null && netInfo.isConnected()) {

                // Check if we have data connection, then lets do an update! if not then schedule update to run
                // whenever we get a connection.
                Intent service = new Intent(context, UpdateService.class);
                Log.d(Constants.TAG, "Internet available. Starting wakeful service");
                startWakefulService(context, service);
            } else {
                // Enable receiver to schedule update when internet is available.
                Log.d(Constants.TAG, "No Internet available. Setting up ConnectivityListener");
                ConnectivityReceiver.enableReceiver(context);
            }
        }
    }
}
