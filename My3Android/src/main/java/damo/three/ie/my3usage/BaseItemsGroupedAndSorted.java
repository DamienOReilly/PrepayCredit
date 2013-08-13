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

import damo.three.ie.my3usage.items.Other;
import damo.three.ie.my3usage.items.OutOfBundle;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.List;

public class BaseItemsGroupedAndSorted {

    private final List<BaseItem> baseItems;
    private String groupName;
    private GroupType groupType;
    private final LocalDate dateNow;

    public BaseItemsGroupedAndSorted(List<BaseItem> baseItemCommons) {

        this.baseItems = baseItemCommons;
        DateTime now = new DateTime();
        dateNow = now.toLocalDate();
        updateGroupName();

    }

    /**
     * Add some meta-data to the usage groups
     */
    private void updateGroupName() {
        String toProcess = baseItems.get(0).getValue1formatted();

        if (baseItems.get(0) instanceof Other) {
            groupName = "Other:";
            groupType = GroupType.GOOD;
        } else if (baseItems.get(0) instanceof OutOfBundle) {
            groupName = "Internet out-of-bundle charges!";
            groupType = GroupType.BAD;
        } else if (toProcess.equals("Won't expire")) {
            groupName = "Won't expire";
            groupType = GroupType.GOOD;
        } else if (toProcess.equals("Queued")) {
            groupName = "Queued";
            groupType = GroupType.GOOD;
        } else {

            DateTime groupDateTime = new DateTime(baseItems.get(0).getValue1().longValue());
            LocalDate localGroupDate = groupDateTime.toLocalDate();
            // get number of days between dates rather than number of absolute days based on hours difference
            int days = Days.daysBetween(dateNow.toDateTimeAtStartOfDay(), localGroupDate.toDateTimeAtStartOfDay()).getDays();

            if (days > 5) {
                groupType = GroupType.GOOD;
            } else if (days >= 3 && days <= 5) {
                groupType = GroupType.WARNING;
            } else {
                groupType = GroupType.BAD;
            }

            groupName = "Expires: " + toProcess;

            if (days == 0) {
                groupName += " (Today)";
            } else if (days == 1) {
                groupName += " (Tomorrow)";
            } else {
                groupName += " (in " + days + " days)";
            }

        }

    }

    public String getGroupName() {
        return groupName;
    }

    public List<BaseItem> getBaseItems() {
        return baseItems;
    }

    public GroupType getGroupType() {
        return groupType;
    }

}