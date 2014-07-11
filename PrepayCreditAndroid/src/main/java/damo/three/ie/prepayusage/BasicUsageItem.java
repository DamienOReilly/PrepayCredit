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

package damo.three.ie.prepayusage;

import damo.three.ie.util.DateUtils;
import org.joda.time.DateTime;

import java.text.ParseException;

/**
 * Class to handle basic usage items. Quantity & expire date
 *
 * @author Damien O'Reilly
 */
public abstract class BasicUsageItem extends UsageItem implements Comparable<BasicUsageItem> {

    protected Number value2;
    private Number value1;

    protected BasicUsageItem(String itemName) {
        super(itemName);
    }

    public String getValue1formatted() {
        return DateUtils.formatDate(value1);
    }

    public Number getValue1() {
        return value1;
    }

    protected void setValue1(String value1str) {
        value1 = DateUtils.parseDate(value1str);
    }

    public abstract String getValue2formatted();

    public abstract void setValue2(String value2str) throws ParseException;

    /**
     * Just a method to check whether the usage item is expired past current date.
     *
     * @return boolean
     */
    public boolean isNotExpired() {
        DateTime now = new DateTime().withTimeAtStartOfDay();
        // +1 day as they expire at 00:00:00am of the next day really.
        return new DateTime(value1.longValue()).plusDays(1).withTimeAtStartOfDay().compareTo(now) > 0;
    }

    /**
     * Sometimes I've seen items as "Won't expire" that look to be add-ons due to the €20 topup e.g. text, data
     * Normally these expire within 30 days. Not sure in what scenario they are non expireable (I know cash credit
     * mostly doesn't expire).
     *
     * @return boolean
     */
    public boolean isExpirable() {
        return value1.longValue() != DateUtils.WONT_EXPIRE;
    }

    /**
     * Comparison, used for grouping of {@code BasicUsageItem} based on expire date.
     *
     * @param that BaseUsageItem to compare against
     * @return Comparison result.
     */
    @Override
    public int compareTo(BasicUsageItem that) {
        return this.getValue1().longValue() > that.getValue1().longValue() ? 1 : this.getValue1().longValue() < that
                .getValue1().longValue() ? -1 : 0;
    }

}