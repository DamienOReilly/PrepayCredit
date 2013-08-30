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

package damo.three.ie.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.internal.widget.IcsLinearLayout;
import damo.three.ie.R;
import damo.three.ie.prepayusage.BaseItem;
import damo.three.ie.prepayusage.BaseItemsGroupedAndSorted;
import damo.three.ie.prepayusage.GroupType;

public class UsageView extends LinearLayout {

    /**
     * LinearLayout for hosting the users usages
     *
     * @param context                   Application Context
     * @param baseItemsGroupedAndSorted Usages
     */
    public UsageView(Context context,
                     BaseItemsGroupedAndSorted baseItemsGroupedAndSorted) {
        super(context);

        // Get the group usage layout
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater
                .inflate(R.layout.usage_group_container, null);

        // Add the Header to this group title
        TextView groupTitle = (TextView) view
                .findViewById(R.id.textViewExtraInfo);

        // Color it if necessary
        if (baseItemsGroupedAndSorted.getGroupType() == GroupType.WARNING) {
            groupTitle.setTextColor(getResources().getColor(R.color.orange));
        } else if (baseItemsGroupedAndSorted.getGroupType() == GroupType.BAD) {
            groupTitle.setTextColor(getResources().getColor(R.color.red));
        }
        groupTitle.setText(baseItemsGroupedAndSorted.getGroupName());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(15, 15, 15, 0);

        // grab the LinearLayout that will handle "item" and "value"
        // Using ActionBar Sherlock's LinearLayout as it supports dividers
        // Normally API 11+ only supports this.
        IcsLinearLayout usageItemsView = (IcsLinearLayout) view
                .findViewById(R.id.usage_items_view);

        // Add each item and its value to the layout
        for (BaseItem baseItem : baseItemsGroupedAndSorted.getBaseItems()) {

            View usageItemView = layoutInflater.inflate(R.layout.usageitems,
                    null);
            TextView title = (TextView) usageItemView
                    .findViewById(R.id.textViewItemName);
            TextView info = (TextView) usageItemView
                    .findViewById(R.id.textViewItemValue);

            title.setText(baseItem.getTitle());
            info.setText(baseItem.getValue2formatted());

            LinearLayout.LayoutParams layoutParamsUsageItem = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            usageItemsView.addView(usageItemView, layoutParamsUsageItem);

        }

        addView(view, layoutParams);
    }
}