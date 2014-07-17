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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import damo.three.ie.prepayusage.BasicUsageItem;
import damo.three.ie.prepayusage.UsageItem;
import damo.three.ie.util.JSONUtils;
import damo.three.ie.util.UsageUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * This class is executed when the phone boots up to set internet add-on expiring alarms. As alarms aren't
 * persisted across reboots on Android.
 */
public class InternetAddonAlarmManagerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPrefsUsages = context.getSharedPreferences("damo.three.ie.previous_usage",
                Context.MODE_PRIVATE);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean notificationsEnabled = sharedPrefs.getBoolean("notification", true);
        String usages = sharedPrefsUsages.getString("usage_info", null);

        // first check if anything was persisted
        if (usages != null) {
            try {
                List<UsageItem> usageItems = JSONUtils.jsonToUsageItems(new JSONArray(usages));
                List<BasicUsageItem> basicUsageItems = UsageUtils.getAllBasicItems(usageItems);
                UsageUtils.registerInternetExpireAlarm(context, basicUsageItems, notificationsEnabled, false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
