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

package damo.three.ie.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import damo.three.ie.prepay.Constants;
import damo.three.ie.prepay.InternetAddonExpireReceiver;
import damo.three.ie.prepay.UpdateReceiver;
import damo.three.ie.prepayusage.*;
import damo.three.ie.prepayusage.items.Data;
import damo.three.ie.prepayusage.items.InternetAddon;
import damo.three.ie.prepayusage.items.OutOfBundle;
import org.joda.time.DateTime;

import java.util.*;

/**
 * @author Damien O'Reilly
 */
public class UsageUtils {

    /**
     * Retrieve all basic usage items from the entire usage list.
     *
     * @param usageItems All usage items.
     * @return Basic usage items.
     */
    public static List<BasicUsageItem> getAllBasicItems(List<UsageItem> usageItems) {
        List<BasicUsageItem> basicUsageItems = new ArrayList<BasicUsageItem>();
        for (UsageItem usageItem : usageItems) {
            if (usageItem instanceof BasicUsageItem) {
                basicUsageItems.add((BasicUsageItem) usageItem);
            }
        }
        return basicUsageItems;
    }

    /**
     * Organise the usages into related groups
     *
     * @return usages grouped
     */
    public static List<BasicUsageItemsGrouped> groupUsages(List<BasicUsageItem> basicUsageItems) {

        // Sort the items, based on expire time
        BasicUsageItemExpireSorter basicUsageItemExpireSorter = new BasicUsageItemExpireSorter();
        Collections.sort(basicUsageItems, new BasicUsageItemExpireSorter());

        // Get the unique item types, based on expire time.
        SortedSet<BasicUsageItem> basicUsageItemCommonsSet = new TreeSet<BasicUsageItem>(basicUsageItemExpireSorter);
        basicUsageItemCommonsSet.addAll(basicUsageItems);

        List<BasicUsageItemsGrouped> basicUsageItemsGrouped = new ArrayList<BasicUsageItemsGrouped>();

        //Group items of the same type into their own group (this is based on same expire date).
        List<BasicUsageItem> tmpBasicUsageItems;
        for (BasicUsageItem a : basicUsageItemCommonsSet) {
            tmpBasicUsageItems = new ArrayList<BasicUsageItem>();

            for (BasicUsageItem b : basicUsageItems) {
                if (a.dateEquals(b) == 0) {
                    tmpBasicUsageItems.add(b);
                }
            }
            basicUsageItemsGrouped.add(new BasicUsageItemsGrouped(tmpBasicUsageItems));
        }

        // Finally, sort the groups themselves based on expiring date, if any.
        Collections.sort(basicUsageItemsGrouped);

        return basicUsageItemsGrouped;
    }

    /**
     * Returns all the out of bundle items.
     *
     * @param usageItems Usages to search through.
     * @return Out of bundle items.
     */
    public static List<OutOfBundle> getAllOutOfBundleItems(List<UsageItem> usageItems) {

        List<OutOfBundle> outOfBundleList = new ArrayList<OutOfBundle>();

        for (UsageItem usageItem : usageItems) {
            if (usageItem instanceof OutOfBundle) {
                outOfBundleList.add((OutOfBundle) usageItem);
            }
        }
        return outOfBundleList;
    }

    /**
     * Register alarm when last internet data/add-on expires.
     * Clear alarms if they are no longer relevant.
     */
    public static void registerInternetExpireAlarm(Context context, List<BasicUsageItem> usageItems,
                                                   boolean notificationsEnabled, boolean obeyThreshold) {
        InternetUsageRegistry internetUsageRegistry = InternetUsageRegistry.getInstance();
        internetUsageRegistry.clear();

        /* For each Internet add-on, add it to InternetUsageRegistry */
        for (BasicUsageItem b : usageItems) {
            if (b instanceof InternetAddon || b instanceof Data) {
                /* Make sure it's not expired first */
                if (b.isExpirable() && b.isNotExpired()) {
                    internetUsageRegistry.submit(b.getExpireDate());
                }
            }
        }

        DateTime dateTime = internetUsageRegistry.getDateTime();

        Intent intent = new Intent(context, InternetAddonExpireReceiver.class);

        // Add internet expire time to intent. We need this if the add-on expired while the phone was off.
        // We will use this to determine the appropriate information to show the user.
        if (dateTime != null) {
            intent.putExtra(InternetUsageRegistry.INTERNET_EXPIRE_TIME,
                    internetUsageRegistry.getDateTime().getMillis());
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // First check if anything is registered in internet registry and we want notifications
        if (internetUsageRegistry.getDateTime() != null && notificationsEnabled) {
            am.cancel(pendingIntent);

            //Set one-time alarm. 4 hours enough of a warning ? Decide if we wan't to make this configurable.
            DateTime internetExpires = internetUsageRegistry.getDateTime().minusHours(4);
            // If obeying threshold, don't show a notification if internet is expiring within 4 hours, as we already
            // showed a notification at the 4 hour to expiry mark. This prevents a notification every time the user
            // refreshes the usages while it is under 4 hours until expire time.
            if (obeyThreshold) {
                if (new DateTime().compareTo(internetExpires) < 0) {
                    am.set(AlarmManager.RTC_WAKEUP, internetExpires.getMillis(), pendingIntent);
                }
            } else {
                // This option exists if we do want a notification shown within the 4 hour threshold. e.g. user turns on
                // the phone within the 4 hour threshold to expirey time.
                am.set(AlarmManager.RTC_WAKEUP, internetExpires.getMillis(), pendingIntent);
            }
        } else {
            am.cancel(pendingIntent);
        }
    }

    /**
     * Responsible for enabling alarms for background updating.
     */
    public static void setupBackgroundUpdateAlarms(Context context, boolean enabled) {
        Intent receiver = new Intent(context, UpdateReceiver.class);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, receiver,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (enabled) {
            Log.d(Constants.TAG, "Setting up repeating UpdateReceiver alarms. (UsageUtils)");
            Calendar calendar = Calendar.getInstance();
            // Schedule alarm for refreshing as normal.
            if (calendar.get(Calendar.HOUR_OF_DAY) >= Constants.HOUR_TO_UPDATE) {
                // Starting tomorrow
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            calendar.set(Calendar.HOUR_OF_DAY, Constants.HOUR_TO_UPDATE);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            am.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        } else {
            Log.d(Constants.TAG, "Cancelling repeating UpdateReceiver alarms. (UsageUtils)");
            am.cancel(pendingIntent);
        }
    }
}