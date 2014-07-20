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
import damo.three.ie.prepayusage.BasicUsageItemTypeSorter;
import damo.three.ie.prepayusage.BasicUsageItemsGrouped;
import damo.three.ie.prepayusage.ExpireGroupType;

import java.util.*;

/**
 * LinearLayout for hosting the users basic usages
 * TODO: simplify this class. It has gotten pretty messy.
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
            groupTitle.setTextColor(getResources().getColor(R.color.holo_orange_light));
        } else if (basicUsageItemsGrouped.getExpireGroupType() == ExpireGroupType.BAD) {
            groupTitle.setTextColor(getResources().getColor(R.color.holo_red_light));
        }
        groupTitle.setText(basicUsageItemsGrouped.getExpireGroup());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(15, 15, 15, 0);

        LinearLayout usageItemsView = (LinearLayout) view.findViewById(R.id.basic_usage_items_view);

        List<BasicUsageItem> basicUsageItems = basicUsageItemsGrouped.getBasicUsageItems();
        SortedSet<BasicUsageItem> basicUsageItemTypeSet = new TreeSet<BasicUsageItem>(
                new BasicUsageItemTypeSorter());

        basicUsageItemTypeSet.addAll(basicUsageItems);

        // Get a list of usage item types where there is more than 1 instance.
        List<String> searchList = new ArrayList<String>();
        for (BasicUsageItem a : basicUsageItemTypeSet) {
            if (Collections.frequency(basicUsageItems, a) > 1) {
                searchList.add(a.getItemName());
            }
        }

        // Group same items together. e.g. All occurrences of "Data" in a BasicUsageItemsGrouped will be merged
        // together.
        List<List<BasicUsageItem>> mergeList = new ArrayList<List<BasicUsageItem>>();
        for (String item : searchList) {
            List<BasicUsageItem> itemsToMerge = new ArrayList<BasicUsageItem>();
            for (BasicUsageItem a : basicUsageItems) {
                if (a.getItemName().equals(item)) {
                    itemsToMerge.add(a);
                }
            }
            mergeList.add(itemsToMerge);
            basicUsageItems.removeAll(itemsToMerge);
        }

        // Add each item and its value to the merged layout
        for (BasicUsageItem basicUsageItem : basicUsageItemsGrouped.getBasicUsageItems()) {

            View usageItemView = layoutInflater.inflate(R.layout.basic_usage_item, usageItemsView, false);
            TextView title = (TextView) usageItemView.findViewById(R.id.textViewItemName);
            TextView info = (TextView) usageItemView.findViewById(R.id.textViewItemValue);

            title.setText(basicUsageItem.getItemName());
            info.setText(basicUsageItem.getQuantityFormatted());

            LinearLayout.LayoutParams layoutParamsUsageItem = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            usageItemsView.addView(usageItemView, layoutParamsUsageItem);
        }

        // For each merged groups, accumulate up their quantities and create MergedItem POJO.
        if (mergeList.size() > 0) {
            List<MergedItem> mergedItems = new ArrayList<MergedItem>();
            for (List<BasicUsageItem> subList : mergeList) {
                MergedItem mergedItem = new MergedItem();
                mergedItem.setName(subList.get(0).getItemName());

                List<Number> toSum = new ArrayList<Number>();
                List<String> itemCosts = new ArrayList<String>();
                String combinedValue = "";
                for (BasicUsageItem item : subList) {
                    itemCosts.add(item.getQuantityFormatted());
                    toSum.add(item.getQuantity());
                    combinedValue = item.mergeQuantity(toSum);
                }
                mergedItem.setCombinedValue(combinedValue);
                mergedItem.setChildItems(itemCosts);

                mergedItems.add(mergedItem);
            }

            // For each MergedItem, populate its layout
            for (MergedItem mergedItem : mergedItems) {
                LinearLayout mergeGroupLayout = (LinearLayout) layoutInflater.inflate(R.layout.merged_group,
                        usageItemsView, false);
                final TextView mergedGroupName = (TextView) mergeGroupLayout.findViewById(R.id.merged_group_name);
                final TextView mergedGroupTotal = (TextView) mergeGroupLayout.findViewById(R.id
                        .merged_group_combined_total);
                mergedGroupName.setText(mergedItem.getName());
                mergedGroupTotal.setText(mergedItem.getCombinedValue());
                final LinearLayout childLinearLayout = (LinearLayout) mergeGroupLayout.findViewById(
                        R.id.merge_group_child_list);

                // Action to perform when clicking on the expander button/total text field.
                mergedGroupTotal.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (childLinearLayout.isShown()) {
                            childLinearLayout.setVisibility(View.GONE);
                            mergedGroupTotal.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                    R.drawable.expander_open_holo_light, 0);
                        } else {
                            childLinearLayout.setVisibility(View.VISIBLE);
                            mergedGroupTotal.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                    R.drawable.expander_close_holo_light, 0);
                        }
                    }
                });

                // Add all the child items in the MergedItem to its own view. Initially it is set to View.GONE.
                for (String childItem : mergedItem.getChildItems()) {
                    LinearLayout childLayout = (LinearLayout) layoutInflater.inflate(R.layout.merged_group_child_item,
                            mergeGroupLayout, false);

                    TextView mergeChildItem = (TextView) childLayout.findViewById(R.id.merged_child_item);
                    mergeChildItem.setText(childItem);

                    LinearLayout.LayoutParams layoutParamsChild = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                            .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    childLinearLayout.addView(childLayout, layoutParamsChild);

                }

                // Finally, add the MergedItem to the usage view, below the individual usage items for thisparticular
                // group.
                LinearLayout.LayoutParams layoutParamsMerged = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                usageItemsView.addView(mergeGroupLayout, layoutParamsMerged);
            }
        }
        // All done, add the usage group.
        addView(view, layoutParams);
    }

    /**
     * Inner class to simplify dealing with usage items to merge.  Class resembles Java Bean.
     */
    class MergedItem {
        private String name;
        private String combinedValue;
        private List<String> childItems;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCombinedValue() {
            return combinedValue;
        }

        public void setCombinedValue(String combinedValue) {
            this.combinedValue = combinedValue;
        }

        public List<String> getChildItems() {
            return childItems;
        }

        public void setChildItems(List<String> childItems) {
            this.childItems = childItems;
        }
    }
}