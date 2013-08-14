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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * Comes in as '20<sup>th</sup> June 2013'
     * My3 can give us months named with first 4 chars. E.g. 'August' is displayed on the usage page as 'Augu' !!
     * Since this doesn't comply with any Date format parser, convert it to 3 chars that is accepted by the
     * 'MMM' pattern.
     *
     * @param outOfBundleDate Out of Bundle date
     * @return Out of bundle date as {@link Long}
     */
    public static Long parseOutOfBundleDate(String outOfBundleDate) {

        //Strip off HTML tags and other stuff
        String cleaned = outOfBundleDate.replace("<sup>", "")
                .replace("</sup>", "").replace("Expires ", "").replaceAll("(?:st|nd|rd|th)", "").trim();

        Pattern p = Pattern.compile("(\\d+)\\s(.*)\\s(\\d{4})");
        Matcher m = p.matcher(cleaned);

        StringBuilder cleanedDate = new StringBuilder();
        while (m.find()) {
            cleanedDate.append(m.group(1));
            cleanedDate.append(' ');
            cleanedDate.append(m.group(2).substring(0, 3));
            cleanedDate.append(' ');
            cleanedDate.append(m.group(3));
        }

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMM yyyy").withLocale(Locale.UK);
        DateTime dt = formatter.parseDateTime(cleanedDate.toString());

        return dt.getMillis();
    }

}