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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Class executes an update of usages when internet activity is available again. This is only enabled once off when we
 * previously tried up update in the background and internet connectivity was unavailable.
 *
 * @author Damien O'Reilly
 */
public class ConnectivityReceiver extends WakefulBroadcastReceiver {

    /**
     * Enables ConnectivityReceiver
     */
    public static void enableReceiver(Context context) {
        ComponentName component = new ComponentName(context, ConnectivityReceiver.class);
        context.getPackageManager().setComponentEnabledSetting(component,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * Disables ConnectivityReceiver
     */
    private static void disableReceiver(Context context) {
        ComponentName component = new ComponentName(context, ConnectivityReceiver.class);
        context.getPackageManager().setComponentEnabledSetting(component,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPrefs.getBoolean("backgroundupdate", true) && !(sharedPrefs.getString("mobile", "").equals("")
                && sharedPrefs.getString("password", "").equals(""))) {

            Log.d(Constants.TAG, "Internet back!! updating usage!");

            Intent receiver = new Intent(context, UpdateReceiver.class);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            // Using different request code to 0 so it won't conflict main repeating alarm.
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 2, receiver,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            // Keeping efficiency in mind:
            // http://developer.android.com/reference/android/app/AlarmManager.html#ELAPSED_REALTIME
            am.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), pendingIntent);

            // Disable receiver after we scheduled an update.
            disableReceiver(context);
        }
    }
}