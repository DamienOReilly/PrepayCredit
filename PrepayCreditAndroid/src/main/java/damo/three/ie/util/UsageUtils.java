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

package damo.three.ie.util;

import damo.three.ie.prepayusage.BasicUsageItem;
import damo.three.ie.prepayusage.BasicUsageItemsGrouped;
import damo.three.ie.prepayusage.UsageItem;
import damo.three.ie.prepayusage.items.OutOfBundle;

import java.util.*;

/**
 * @author Damien O'Reilly
 */
public class UsageUtils {

    /**
     * Retrieve all basic usage items from the entire usage list.
     *
     * @param usageItems All usage items.
     * @return Basic usage items.
     */
    public static List<BasicUsageItem> getAllBasicItems(List<UsageItem> usageItems) {
        List<BasicUsageItem> basicUsageItems = new ArrayList<BasicUsageItem>();
        for (UsageItem usageItem : usageItems) {
            if (usageItem instanceof BasicUsageItem) {
                basicUsageItems.add((BasicUsageItem) usageItem);
            }
        }
        return basicUsageItems;
    }

    /**
     * Organise the usages into related groups
     *
     * @return usages grouped
     */
    public static List<BasicUsageItemsGrouped> groupUsages(List<BasicUsageItem> basicUsageItems) {

        List<BasicUsageItemsGrouped> basicUsageItemsGrouped = new ArrayList<BasicUsageItemsGrouped>();

        // Sort the items
        Collections.sort(basicUsageItems);

        // Get the unique item types
        SortedSet<BasicUsageItem> basicUsageItemCommonsSet = new TreeSet<BasicUsageItem>(basicUsageItems);

        List<BasicUsageItem> tmpBasicUsageItems;

        //Group items of the same type into their own group.
        for (BasicUsageItem a : basicUsageItemCommonsSet) {

            tmpBasicUsageItems = new ArrayList<BasicUsageItem>();

            for (BasicUsageItem b : basicUsageItems) {
                if (a.compareTo(b) == 0) {
                    tmpBasicUsageItems.add(b);
                }
            }

            basicUsageItemsGrouped.add(new BasicUsageItemsGrouped(tmpBasicUsageItems));
        }

        // Finally, sort the groups themselves based on expiring date, if any.
        Collections.sort(basicUsageItemsGrouped);

        return basicUsageItemsGrouped;
    }

    /**
     * Returns all the out of bundle items.
     *
     * @param usageItems Usages to search through.
     * @return Out of bundle items.
     */
    public static List<OutOfBundle> getAllOutOfBundleItems(List<UsageItem> usageItems) {

        List<OutOfBundle> outOfBundleList = new ArrayList<OutOfBundle>();

        for (UsageItem usageItem : usageItems) {
            if (usageItem instanceof OutOfBundle) {
                outOfBundleList.add((OutOfBundle) usageItem);
            }
        }
        return outOfBundleList;
    }
}