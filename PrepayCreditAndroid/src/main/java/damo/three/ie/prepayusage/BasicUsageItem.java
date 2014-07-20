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
import java.util.List;

/**
 * Class to handle basic usage items. Quantity & expire date
 *
 * @author Damien O'Reilly
 */
public abstract class BasicUsageItem extends UsageItem {

    private long expireDate;

    protected BasicUsageItem(String itemName) {
        super(itemName);
    }

    public long getExpireDate() {
        return expireDate;
    }

    protected void setExpireDate(String expireDateStr) {
        expireDate = DateUtils.parseDate(expireDateStr);
    }

    public abstract String getQuantityFormatted();

    public abstract void setQuantityFormatted(String quantityStr) throws ParseException;

    public abstract Number getQuantity();

    public abstract String mergeQuantity(List<Number> toSum);

    /**
     * Just a method to check whether the usage item is expired past current date.
     *
     * @return boolean
     */
    public boolean isNotExpired() {
        DateTime now = new DateTime().withTimeAtStartOfDay();
        // +1 day as they expire at 00:00:00am of the next day really.
        return new DateTime(expireDate).plusDays(1).withTimeAtStartOfDay().compareTo(now) > 0;
    }

    /**
     * Sometimes I've seen items as "Won't expire" that look to be add-ons due to the €20 topup e.g. text, data
     * Normally these expire within 30 days. Not sure in what scenario they are non expireable (I know cash credit
     * mostly doesn't expire).
     *
     * @return boolean
     */
    public boolean isExpirable() {
        return expireDate != DateUtils.WONT_EXPIRE;
    }

    /**
     * Comparison, used for grouping of {@code BasicUsageItem} based on expire date only.
     *
     * @param o To compare against.
     * @return True of equal, otherwise false.
     */
    public int dateEquals(Object o) {
        BasicUsageItem that = (BasicUsageItem) o;
        return this.getExpireDate() > that.getExpireDate() ? 1 : this.getExpireDate() < that.getExpireDate() ? -1 : 0;
    }

    /**
     * Comparison, used for grouping of {@code BasicUsageItem} based on item type only.
     *
     * @param o To compare against.
     * @return True of equal, otherwise false.
     */
    @Override
    public boolean equals(Object o) {
        BasicUsageItem that = (BasicUsageItem) o;
        return this.getItemName().equals(that.getItemName());
    }
}