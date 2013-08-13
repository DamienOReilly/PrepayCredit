/*
 * This file is part of My3 Prepay for Android
 *
 * Copyright © 2013  Damien O'Reilly
 *
 * My3 Prepay for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * My3 Prepay for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with My3 Prepay for Android.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Report bugs or new features at: https://github.com/DamienOReilly/My3Usage
 * Contact the author at:          damienreilly@gmail.com
 */

package damo.three.ie.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Locale;

public class DateUtils {

    /**
     * Return a string representation of the current date now.
     *
     * @return {@link String}
     */
    public static String dateNowAsString() {

        DateTime dateTime = new DateTime();
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.mediumDateTime();

        return dateTimeFormatter.print(dateTime);

    }

    /**
     * Convert a date as milliseconds to a string representation
     *
     * @param input Date in milliseconds
     * @return {@link String}
     */
    public static String formatDate(Number input) {

        if (input == null) {
            return "Won't expire";
        } else if (input.longValue() == -1234L) {
            return "Queued";
        }

        DateTime dateTime = new DateTime(input.longValue());
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.mediumDate();

        return dateTimeFormatter.print(dateTime);
    }

    /**
     * Convert a date as {@link String} to milliseconds as {@link Long}.
     *
     * @param input Date as String
     * @return Date object from inputted string. Null = doesn't expire. -1234 =
     *         Queued Item.
     */
    public static Long parseDate(String input) {

        if (input.equals("Today")) {
            return Calendar.getInstance().getTime().getTime();
        } else if (input.equals("Won't expire**")) {
            return null;
        } else if (input.equals("In queue")) {
            return -1234L;
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

        // Comes in as 20<sup>th</sup> June 2013
        String cleaned = outOfBundleDate.replace("<sup>", "")
                .replace("</sup>", "").replaceAll("(?:st|nd|rd|th)", "").trim();

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMMM yyyy").withLocale(Locale.UK);
        DateTime dt = formatter.parseDateTime(cleaned.replace("Expires ", ""));

        return dt.getMillis();

    }

}
