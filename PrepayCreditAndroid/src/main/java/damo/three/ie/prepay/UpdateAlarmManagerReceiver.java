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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

/**
 * Schedules alarms which perform background updates. This class is invoked at boot-up.
 *
 * @author Damien O'Reilly
 */
public class UpdateAlarmManagerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(Constants.TAG, "In UpdateAlarmManagerReceiver");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sharedUsagePref = context.getSharedPreferences("damo.three.ie.previous_usage",
                Context.MODE_PRIVATE);

        long lastRefresh = sharedUsagePref.getLong("last_refreshed_milliseconds", 0);

        // Check if background updates are enabled and credentials are set.
        if (sharedPrefs.getBoolean("backgroundupdate", true) && !(sharedPrefs.getString("mobile", "").equals("")
                && sharedPrefs.getString("password", "").equals(""))) {

            Intent receiver = new Intent(context, UpdateReceiver.class);

            // If we are passed our selected update hour...
            Calendar calendar = Calendar.getInstance();
            if ((calendar.get(Calendar.HOUR_OF_DAY) >= Constants.HOUR_TO_UPDATE)) {

                // and we haven't refreshed today, then kick off a refresh now
                if (noRefreshDoneToday(calendar, lastRefresh)) {
                    Log.d(Constants.TAG, "Past our alarm time and no refresh done, calling UpdateReceiver now and " +
                            "re-scheduling for tomorrow");

                    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    // Using different request code to 0 so it won't conflict with other alarm below.
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, receiver,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    // Keeping efficiency in mind:
                    // http://developer.android.com/reference/android/app/AlarmManager.html#ELAPSED_REALTIME
                    am.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), pendingIntent);
                } else {
                    // else, if we already refreshed, then re-schedule again for tomorrow.
                    Log.d(Constants.TAG, "Past our alarm time but refresh already done, re-scheduling for tomorrow");
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }
            }

            // Schedule alarm for refreshing as normal.
            calendar.set(Calendar.HOUR_OF_DAY, Constants.HOUR_TO_UPDATE);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, receiver,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Log.d(Constants.TAG, "Setting up repeating UpdateReceiver alarms. (Boot receiver)");

            // Keeping efficiency in mind:
            // http://developer.android.com/reference/android/app/AlarmManager.html#setInexactRepeating(int, long,
            // long, android.app.PendingIntent)
            // http://developer.android.com/reference/android/app/AlarmManager.html#RTC
            am.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
    }

    /**
     * Just checks if a refresh was done already today since {@link damo.three.ie.prepay
     * .Constants#HOUR_TO_UPDATE} hour
     * of the day. This will prevent refreshing on every reboot if a refresh was already done. Otherwise, the app
     * behaviour is: if phone was rebooted and if no refresh was done and its past the
     * {@link damo.three.ie.prepay.Constants#HOUR_TO_UPDATE} hour, schedule a refresh immediately, and if that is
     * successful, last refresh time is updated, so a subsequent reboot won't trigger a refresh.
     *
     * @param calendarNow Date now.
     * @param lastRefresh Last refresh time in milliseconds.
     * @return True if refresh is to be done, otherwise false.
     */
    private boolean noRefreshDoneToday(Calendar calendarNow, long lastRefresh) {
        Calendar calendarLastRefreshed = Calendar.getInstance();
        calendarLastRefreshed.setTimeInMillis(lastRefresh);

        return (calendarNow.get(Calendar.DAY_OF_MONTH) != calendarLastRefreshed.get(Calendar.DAY_OF_MONTH));
    }
}
