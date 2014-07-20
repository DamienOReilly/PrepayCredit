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
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import damo.three.ie.prepayusage.BasicUsageItem;
import damo.three.ie.prepayusage.UsageItem;
import damo.three.ie.util.JSONUtils;
import damo.three.ie.util.UsageUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;

import java.util.Calendar;
import java.util.List;

/**
 * Service responsible for the background fetch of user usages. Works silently.
 *
 * @author Damien O'Reilly
 */
public class UpdateService extends IntentService {

    public UpdateService() {
        super("PrepayUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Context context = getApplicationContext();
        try {
            Log.d(Constants.TAG, "Fetching usages from service.");
            UsageFetcher usageFetcher = new UsageFetcher(context, true);
            JSONArray usages = usageFetcher.getUsages();

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences sharedUsagePref = context.getSharedPreferences("damo.three.ie.previous_usage",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedUsagePref.edit();
            editor.putLong("last_refreshed_milliseconds", new DateTime().getMillis());
            editor.putString("usage_info", usages.toString());
            editor.commit();


            // Register alarms for newly refreshed usages in background
            boolean notificationsEnabled = sharedPref.getBoolean("notification", true);
            List<UsageItem> usageItems = JSONUtils.jsonToUsageItems(usages);
            List<BasicUsageItem> basicUsageItems = UsageUtils.getAllBasicItems(usageItems);
            UsageUtils.registerInternetExpireAlarm(context, basicUsageItems, notificationsEnabled, true);

        } catch (Exception e) {
            // Try again at 7pm, unless its past 7pm. Then forget and let tomorrow's alarm do the updating.
            // Still trying to decide if I need a more robust retry mechanism.
            Log.d(Constants.TAG, "Caught exception: " + e.getLocalizedMessage());
            Calendar calendar = Calendar.getInstance();
            if (calendar.get(Calendar.HOUR_OF_DAY) < Constants.HOUR_TO_RETRY) {
                Log.d(Constants.TAG, "Scheduling a re-try for 7pm");
                calendar.set(Calendar.HOUR_OF_DAY, Constants.HOUR_TO_RETRY);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                Intent receiver = new Intent(context, UpdateReceiver.class);
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                // Using different request code to 0 so it won't conflict with other alarm below.
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 3, receiver,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                // Keeping efficiency in mind:
                // http://developer.android.com/reference/android/app/AlarmManager.html#ELAPSED_REALTIME
                am.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            }
        } finally {
            Log.d(Constants.TAG, "Finish UpdateService");
            UpdateReceiver.completeWakefulIntent(intent);
        }
    }
}