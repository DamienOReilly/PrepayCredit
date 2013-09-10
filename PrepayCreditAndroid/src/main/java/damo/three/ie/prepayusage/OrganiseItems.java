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

import java.util.*;

public class OrganiseItems {

    private final List<BaseItem> baseItemCommons;
    private final List<AllBaseItemsGroupedAndSorted> allBaseItemsGroupedAndSorted;

    public OrganiseItems(List<BaseItem> baseItemCommons) {
        this.baseItemCommons = baseItemCommons;
        this.allBaseItemsGroupedAndSorted = new ArrayList<AllBaseItemsGroupedAndSorted>();
    }

    /**
     * Organise the usages into related groups
     *
     * @return usages grouped
     */
    public List<AllBaseItemsGroupedAndSorted> groupUsages() {

        // Sort the items
        Collections.sort(baseItemCommons);

        // Get the unique item types
        SortedSet<BaseItem> baseItemCommonsSet = new TreeSet<BaseItem>(
                baseItemCommons);

        List<BaseItem> tmpBaseItems;

        //Group items of the same type into their own group.
        for (BaseItem a : baseItemCommonsSet) {

            tmpBaseItems = new ArrayList<BaseItem>();

            for (BaseItem b : baseItemCommons) {
                if (a.compareTo(b) == 0)
                    tmpBaseItems.add(b);
            }

            allBaseItemsGroupedAndSorted.add(new AllBaseItemsGroupedAndSorted(
                    tmpBaseItems));
        }

        // Finally, sort the groups themselves based on expiring date, if any.
        Collections.sort(allBaseItemsGroupedAndSorted);

        return allBaseItemsGroupedAndSorted;
    }

}
