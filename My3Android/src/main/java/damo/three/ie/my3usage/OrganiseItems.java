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

import java.util.*;

public class OrganiseItems {

    private final List<BaseItem> baseItemCommons;
    private final List<BaseItemsGroupedAndSorted> baseItemsGroupedAndSorted;

    public OrganiseItems(List<BaseItem> baseItemCommons) {
        this.baseItemCommons = baseItemCommons;
        this.baseItemsGroupedAndSorted = new ArrayList<BaseItemsGroupedAndSorted>();
    }

    /**
     * Organise the usages into related groups
     *
     * @return usages grouped
     */
    public List<BaseItemsGroupedAndSorted> groupUsages() {

        Collections.sort(baseItemCommons);
        SortedSet<BaseItem> baseItemCommonsSet = new TreeSet<BaseItem>(
                baseItemCommons);

        List<BaseItem> tmpBaseItems;

        for (BaseItem a : baseItemCommonsSet) {

            tmpBaseItems = new ArrayList<BaseItem>();

            for (BaseItem b : baseItemCommons) {
                if (a.compareTo(b) == 0)
                    tmpBaseItems.add(b);
            }

            baseItemsGroupedAndSorted.add(new BaseItemsGroupedAndSorted(
                    tmpBaseItems));
        }

        return baseItemsGroupedAndSorted;
    }

}
