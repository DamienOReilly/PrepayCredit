/*
 * This file is part of Prepay Credit for Android
 *
 * Copyright © 2013  Damien O'Reilly
 *
 * Prepay Credit for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
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
import android.preference.PreferenceManager;
import damo.three.ie.prepayusage.BasicUsageItem;
import damo.three.ie.prepayusage.InternetUsageRegistry;
import damo.three.ie.prepayusage.UsageItem;
import damo.three.ie.prepayusage.items.Data;
import damo.three.ie.prepayusage.items.InternetAddon;
import damo.three.ie.util.JSONUtils;
import damo.three.ie.util.UsageUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

//TODO: remove any duplicate code with damo.three.ie.fragment.AccountProcessorFragment

/**
 * This class is executed when the phone boots up to set internet add-on expiring alarms. As alarms aren't
 * persisted across reboots on Android.
 */
public class PrepayAlarmManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPrefsUsages = context.getSharedPreferences("damo.three.ie.previous_usage",
                Context.MODE_PRIVATE);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean notificationsEnabled = sharedPrefs.getBoolean("notification", true);
        String usage = sharedPrefsUsages.getString("usage_info", null);

        // first check if anything was persisted
        if (usage != null) {
            try {
                List<UsageItem> usageItems = JSONUtils.jsonToUsageItems(new JSONArray(usage));
                List<BasicUsageItem> basicUsageItems = UsageUtils.getAllBasicItems(usageItems);

                InternetUsageRegistry internetUsageRegistry = InternetUsageRegistry.getInstance();

                /* For each Internet add-on, add it to InternetUsageRegistry */
                for (BasicUsageItem b : basicUsageItems) {
                    if (b instanceof InternetAddon || b instanceof Data) {
                        /* Make sure it's not expired first */
                        if (b.isNotExpired()) {
                            internetUsageRegistry.submit(b.getValue1().longValue());
                        }
                    }
                }
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent myIntent = new Intent(context.getApplicationContext(), UsageNotifier.class);
                // Add internet expire time to intent. We need this if the add-on expired while the phone was off.
                // We will use this to determine the appropriate information to show the user.
                myIntent.putExtra(InternetUsageRegistry.INTERNET_EXPIRE_TIME,
                        internetUsageRegistry.getDateTime().getMillis());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0,
                        myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                if (internetUsageRegistry.getDateTime() != null && notificationsEnabled) {
                    am.cancel(pendingIntent);

                    /**
                     *  Set one-time alarm. 4 hours enough of a warning ?
                     *  Unlike when refreshing, we won't care if there is less than 4 hours between now
                     *  and expire time as its good for user to get a notification when they turn on
                     *  the device.
                     */
                    am.set(AlarmManager.RTC_WAKEUP, internetUsageRegistry.getDateTime().minusHours(4).getMillis(),
                            pendingIntent);
                } else {
                    am.cancel(pendingIntent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
