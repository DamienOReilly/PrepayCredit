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

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * This class simply holds the latest internet usage item.
 * This info will be used for AlarmManager elsewhere.
 */
public class InternetUsageRegistry {

    public static final String INTERNET_EXPIRE_TIME = "expire_time";
    public static final String INTERNET_EXPIRED = "already_expired";

    private static InternetUsageRegistry internetUsageRegistry;
    private DateTime dateTime;

    /**
     * We only ever need one instance of this. Using Singleton pattern as we still
     * want it to be stateful.
     *
     * @return {@link InternetUsageRegistry}
     */
    public static InternetUsageRegistry getInstance() {

        if (internetUsageRegistry == null) {
            internetUsageRegistry = new InternetUsageRegistry();
        }
        return internetUsageRegistry;
    }

    /**
     * Submit internet usage to registry, only accept it if we don't already have a usage, or if
     * current usage is older than one been submitted.
     *
     * @param inLocalDateTimeAsMilliseconds Internet usage expiring date in milliseconds
     */
    public void submit(Long inLocalDateTimeAsMilliseconds) {
        if ((dateTime == null) ||
                (dateTime.compareTo(new DateTime(inLocalDateTimeAsMilliseconds)) <= 0)) {
            /**
             * +1 day as they expire at 00:00:00am of the next day really.
             */
            dateTime = new DateTime(inLocalDateTimeAsMilliseconds).plusDays(1).withTimeAtStartOfDay();
        }
    }

    /**
     * Clear latest registered internet usage
     */
    public void clear() {
        dateTime = null;
    }

    /**
     * Returns the last expiring internet usage item.
     *
     * @return {@link LocalDate}
     */
    public DateTime getDateTime() {
        return dateTime;
    }
}