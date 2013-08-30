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

public abstract class BaseItem implements Comparable<BaseItem> {

    protected String ITEM_NAME;
    protected Number value1;
    protected Number value2;

    /**
     * @return Usage name
     */
    public String getTitle() {
        return ITEM_NAME;
    }

    /**
     * @return First value
     */
    public Number getValue1() {
        return value1;
    }

    /**
     * Compare {@link BaseItem}
     *
     * @param that BaseItem to compare against
     * @return equals or not
     */
    @Override
    public int compareTo(BaseItem that) {
        return this.getValue1formatted().compareTo(that.getValue1formatted());
    }

    public abstract String getValue1formatted();

    public abstract String getValue2formatted();

    /**
     * Just a method to check whether the usage item is expired past current date.
     *
     * @return boolean
     */
    public boolean isNotExpired() {
        DateTime now = new DateTime().withTimeAtStartOfDay();
        // +1 day as they expire at 00:00:00am of the next day really.
        return new DateTime(value1.longValue())
                .plusDays(1)
                .withTimeAtStartOfDay()
                .compareTo(now) > 0;
    }

}