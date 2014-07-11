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
import damo.three.ie.R;
import damo.three.ie.prepayusage.BasicUsageItem;
import damo.three.ie.prepayusage.BasicUsageItemsGrouped;
import damo.three.ie.prepayusage.ExpireGroupType;

/**
 * LinearLayout for hosting the users basic usages
 */
public class BasicUsageLayout extends LinearLayout {

    public BasicUsageLayout(Context context, BasicUsageItemsGrouped basicUsageItemsGrouped) {
        super(context);

        // Get the group usage layout
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.basic_usage_group_container, this, false);

        // Add the Header to this group title
        TextView groupTitle = (TextView) view.findViewById(R.id.textViewExtraInfo);

        // Color it if necessary
        if (basicUsageItemsGrouped.getExpireGroupType() == ExpireGroupType.WARNING) {
            groupTitle.setTextColor(getResources().getColor(R.color.orange));
        } else if (basicUsageItemsGrouped.getExpireGroupType() == ExpireGroupType.BAD) {
            groupTitle.setTextColor(getResources().getColor(R.color.red));
        }
        groupTitle.setText(basicUsageItemsGrouped.getExpireGroup());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(15, 15, 15, 0);

        LinearLayout usageItemsView = (LinearLayout) view.findViewById(R.id.basic_usage_items_view);

        // Add each item and its value to the layout
        for (BasicUsageItem basicUsageItem : basicUsageItemsGrouped.getBasicUsageItems()) {

            View usageItemView = layoutInflater.inflate(R.layout.basic_usage_item, usageItemsView, false);
            TextView title = (TextView) usageItemView.findViewById(R.id.textViewItemName);
            TextView info = (TextView) usageItemView.findViewById(R.id.textViewItemValue);

            title.setText(basicUsageItem.getItemName());
            info.setText(basicUsageItem.getValue2formatted());

            LinearLayout.LayoutParams layoutParamsUsageItem = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            usageItemsView.addView(usageItemView, layoutParamsUsageItem);
        }
        addView(view, layoutParams);
    }
}