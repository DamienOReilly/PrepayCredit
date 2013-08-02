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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

public class NumberUtils {

    /**
     * Format a numeric as string for the users {@link java.util.Locale}
     *
     * @param input Number to format
     * @return {@link String}
     */
    public static String formatNumeric(Number input) {
        return new DecimalFormat("#,###").format(input.longValue());
    }

    /**
     * Format a numeric as float type string for the users {@link java.util.Locale}
     *
     * @param input Number to format
     * @return {@link String}
     */
    public static String formatFloat(Number input) {
        return new DecimalFormat("#,###.00").format(input
                .floatValue());
    }

    /**
     * Format a numeric as currency type string for the users {@link java.util.Locale}
     *
     * @param input Number to format
     * @return {@link String}
     */
    public static String formatMoney(Number input) {
        // EURO symbol
        return new DecimalFormat("\u20AC#,###.00").format(input
                .floatValue());

    }

    /**
     * Convert string representation of integer to {@link Integer} object
     *
     * @param input Convert a string number to {@link Number}
     * @return {@link Number}
     */
    public static Number parseNumeric(String input) throws ParseException {
        // We will use Locale.US on recommendation from:
        // Be wary of the default locale
        // http://developer.android.com/reference/java/util/Locale.html#default_locale
        // otherwise devices with different locales e.g. Locale.FR expect number in different format. like 10,00 while we use 10.00
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        dfs.setGroupingSeparator(',');
        return new DecimalFormat("#,###.##", dfs).parse(input);
    }

    /**
     * Convert a string cash amount to {@link Number}
     *
     * @param input Cash amount as {@link String}
     * @return {@link Number}
     * @throws ParseException
     */
    public static Number parseMoney(String input) throws ParseException {
        // We will use Locale.US on recommendation from:
        // Be wary of the default locale
        // http://developer.android.com/reference/java/util/Locale.html#default_locale
        // otherwise devices with different locales e.g. Locale.FR expect number in different format. like 10,00 while we use 10.00
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        dfs.setGroupingSeparator(',');
        return new DecimalFormat("\u20AC#,###.##", dfs).parse(input);
    }

}
