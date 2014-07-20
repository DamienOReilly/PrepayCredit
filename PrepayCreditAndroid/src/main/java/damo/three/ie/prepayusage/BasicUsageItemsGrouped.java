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

import damo.three.ie.util.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;

/**
 * Class deals with encapsulating a similar group of usage items and adding meta data.
 */
public class BasicUsageItemsGrouped implements Comparable<BasicUsageItemsGrouped> {

    private final List<BasicUsageItem> basicUsageItems;
    private final DateTime dateTimeNow;
    private String expireGroup;
    private ExpireGroupType expireGroupType;
    private DateTime groupDateTime;

    /**
     * Constructor taking in list of basic usage items.
     *
     * @param basicUsageItems List of basic usage items.
     */
    public BasicUsageItemsGrouped(List<BasicUsageItem> basicUsageItems) {
        this.basicUsageItems = basicUsageItems;
        dateTimeNow = new DateTime();
        updateGroupName();
    }

    /**
     * Add some meta-data to the usage groups.
     */
    private void updateGroupName() {
        long date = basicUsageItems.get(0).getExpireDate();
        if (date == DateUtils.WONT_EXPIRE) {
            expireGroup = "Won't expire";
            expireGroupType = ExpireGroupType.GOOD;
            groupDateTime = new DateTime(DateUtils.WONT_EXPIRE);
        } else if (date == DateUtils.QUEUED) {
            expireGroup = "Queued";
            expireGroupType = ExpireGroupType.GOOD;
            groupDateTime = new DateTime(DateUtils.QUEUED);
        } else {
            // Falling down this far.. must be a date so!
            groupDateTime = new DateTime(date);
            int daysRemaining = Days.daysBetween(dateTimeNow.withTimeAtStartOfDay(), getAbsoluteExpireyDateTime())
                    .getDays() - 1;

            if (daysRemaining > 5) {
                expireGroupType = ExpireGroupType.GOOD;
            } else if (daysRemaining >= 3 && daysRemaining <= 5) {
                expireGroupType = ExpireGroupType.WARNING;
            } else {
                expireGroupType = ExpireGroupType.BAD;
            }

            expireGroup = "Expires: " + DateUtils.formatDate(date);
            expireGroup += " (" + new PrettyTime().format(getAbsoluteExpireyDateTime().toDate()) + ")";
        }
    }

    /**
     * Get the group expire description.
     *
     * @return Expire description.
     */
    public String getExpireGroup() {
        //TODO: update ExpireGroupType to include the descriptive strings.
        return expireGroup;
    }

    /**
     * Return usage items in this group.
     *
     * @return List of usage items.
     */
    public List<BasicUsageItem> getBasicUsageItems() {
        return basicUsageItems;
    }

    /**
     * Return group expire type, e.g. expiring on a date or doesn't expire.
     *
     * @return Expire type.
     */
    public ExpireGroupType getExpireGroupType() {
        return expireGroupType;
    }

    /**
     * Compare the UsageItemsGrouped based on date expiring, if any.
     *
     * @param that To compare against/
     * @return Compare result.
     */
    @Override
    public int compareTo(BasicUsageItemsGrouped that) {
        return this.getGroupDateTime().compareTo(that.getGroupDateTime());
    }

    /**
     * The add-on in fact expires at midnight on the following day.
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

    /**
     * Get the group's expire time.
     *
     * @return Expire time.
     */
    DateTime getGroupDateTime() {
        return groupDateTime;
    }
}