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

package damo.three.ie.prepayusage;

import android.util.Log;
import damo.three.ie.prepay.Constants;
import damo.three.ie.prepayusage.items.Other;
import damo.three.ie.prepayusage.items.OutOfBundle;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.List;

public class AllBaseItemsGroupedAndSorted implements Comparable<AllBaseItemsGroupedAndSorted> {

    private final List<BaseItem> baseItems;
    private String expireGroup;
    private ExpireGroupType expireGroupType;
    private final DateTime dateTimeNow;
    private DateTime groupDateTime;


    public AllBaseItemsGroupedAndSorted(List<BaseItem> baseItemCommons) {
        this.baseItems = baseItemCommons;
        this.groupDateTime =
                dateTimeNow = new DateTime();
        updateGroupName();
    }

    /**
     * Add some meta-data to the usage groups
     */
    private void updateGroupName() {
        String toProcess = baseItems.get(0).getValue1formatted();
        Class clazz = baseItems.get(0).getClass();
        Log.i(Constants.TAG, clazz.getName());

        // default for non-expirables, just a date in future, for Comparator reasons!
        // Wed Jan 01 2020 00:00:00 GMT+0000 (GMT Standard Time)
        groupDateTime = new DateTime(1577836800000L);

        if (baseItems.get(0) instanceof Other) {
            expireGroup = "Other:";
            expireGroupType = ExpireGroupType.GOOD;
        } else if (baseItems.get(0) instanceof OutOfBundle) {
            expireGroup = "Internet out-of-bundle charges!";
            expireGroupType = ExpireGroupType.BAD;
        } else if (toProcess.equals("Won't expire")) {
            expireGroup = "Won't expire";
            expireGroupType = ExpireGroupType.GOOD;
        } else if (toProcess.equals("Queued")) {
            expireGroup = "Queued";
            expireGroupType = ExpireGroupType.GOOD;
        } else {

            // falling down this far.. must be a date so!
            groupDateTime = new DateTime(baseItems.get(0).getValue1().longValue());
            int days = Days.daysBetween(dateTimeNow.withTimeAtStartOfDay(),
                    getAbsoluteExpireyDateTime()).getDays() - 1;

            if (days > 5) {
                expireGroupType = ExpireGroupType.GOOD;
            } else if (days >= 3 && days <= 5) {
                expireGroupType = ExpireGroupType.WARNING;
            } else {
                expireGroupType = ExpireGroupType.BAD;
            }

            expireGroup = "Expires: " + toProcess;

            if (days == 0) {
                expireGroup += " (Midnight)";
            } else if (days == 1) {
                expireGroup += " (Tomorrow)";
            } else {
                expireGroup += " (in " + days + " days)";
            }

        }

    }

    public String getExpireGroup() {
        return expireGroup;
    }

    public List<BaseItem> getBaseItems() {
        return baseItems;
    }

    public ExpireGroupType getExpireGroupType() {
        return expireGroupType;
    }

    /**
     * Compare the AllBaseItemsGroupedAndSorted's based on date expiring, if any.
     *
     * @param that to compare against
     * @return compare result
     */
    @Override
    public int compareTo(AllBaseItemsGroupedAndSorted that) {
        return this.dateTimeNow.compareTo(that.dateTimeNow);
    }

    /**
     * the add-on infact expires at midnight on the following day.
     * e.g. an add-on expiring on 23rd August really expires on 24th August at 00:00:00
     * day lights savings is took into consideration. 00:00:00 may not exist twice a year in some TZ's.
     *
     * @return {@link DateTime} of when the UsageGroup exactly expires
     */
    private DateTime getAbsoluteExpireyDateTime() {
        return groupDateTime.plusDays(1).withTimeAtStartOfDay();
    }

    /**
     * Returns whether this UsageGroup is actually no longer relevant based on the current date.
     * i.e expired!
     *
     * @return boolean
     */
    public boolean isNotExpired() {
        DateTime now = new DateTime().withTimeAtStartOfDay();
        return getAbsoluteExpireyDateTime().compareTo(now) > 0;
    }


}