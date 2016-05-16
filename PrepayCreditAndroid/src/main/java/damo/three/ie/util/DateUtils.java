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

package damo.three.ie.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Locale;

public class DateUtils {

    /* Just use dates in the future to influence ordering later */
    public static final Long WONT_EXPIRE = 1893456000000L;
    public static final Long QUEUED = 1577836800000L;
    private static final Long ALREADY_EXPIRED = 0L;

    /**
     * Convert a date as milliseconds to a string representation
     *
     * @param input Date in milliseconds
     * @return {@link String}
     */
    public static String formatDate(Number input) {
        if (input == null || input.longValue() == WONT_EXPIRE) {
            return "Won't expire";
        } else if (input.longValue() == QUEUED) {
            return "Queued";
        }

        DateTime dateTime = new DateTime(input.longValue());
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.mediumDate();

        return dateTimeFormatter.print(dateTime);
    }

    /**
     * Convert a date as milliseconds to a string representation
     *
     * @param input DateTime in milliseconds
     * @return {@link String}
     */
    public static String formatDateTime(long input) {
        DateTime dateTime = new DateTime(input);
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.mediumDateTime();
        return dateTimeFormatter.print(dateTime);
    }

    /**
     * Convert a date as {@link String} to milliseconds as {@link Long}.
     *
     * @param input Date as String
     * @return Date object from inputted string.
     */
    public static Long parseDate(String input) {

        if (input.equals("Today")) {
            return Calendar.getInstance().getTime().getTime();
        } else if (input.equals("Wont expire**")) {
            return WONT_EXPIRE;
        } else if (input.equals("In queue")) {
            return QUEUED;
        } else if (input.equals("Expired")) {
            // for some reason people get usages with expire date of "Expired"
            // it will be filtered out later.
            return ALREADY_EXPIRED;
        } else {

            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yy").withLocale(Locale.UK);
            DateTime dt = formatter.parseDateTime(input.replace("Expires ", ""));
            return dt.getMillis();
        }
    }

    /**
     * Converts an Out of Bundle date as string to {@link Long}
     *
     * @param outOfBundleDate Out of Bundle date
     * @return Out of bundle date as {@link Long}
     */
    public static Long parseOutOfBundleDate(String outOfBundleDate) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMMMM yyyy").withLocale(Locale.UK);
        DateTime dt = formatter.parseDateTime(outOfBundleDate);
        return dt.getMillis();
    }
}