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

package damo.three.ie.my3usage;

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
     * @param other BaseItem to compare against
     * @return equals or not
     */
    @Override
    public int compareTo(BaseItem other) {
        return this.getValue1formatted().compareTo(other.getValue1formatted());
    }

    public abstract String getValue1formatted();

    public abstract String getValue2formatted();

}
