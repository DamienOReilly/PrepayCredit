/*
 * This file is part of Prepay Credit for Android
 *
 * Copyright © 2014  Damien O'Reilly
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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import damo.three.ie.R;
import damo.three.ie.prepayusage.items.OutOfBundle;

import java.util.List;

/**
 * LinearLayout for hosting the users out-of-bundle usages
 */
public class OutOfBundleLayout extends LinearLayout {

    public OutOfBundleLayout(Context context, List<OutOfBundle> outOfBundleList) {
        super(context);

        // Get the group usage layout
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.outofbundle_container, this, false);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(10, 10, 10, 10);

        LinearLayout outOfBundleItemsView = (LinearLayout) view.findViewById(R.id.outofbundle_view);

        for (OutOfBundle outOfBundle : outOfBundleList) {
            View usageItemView = layoutInflater.inflate(R.layout.outofbundle_item, outOfBundleItemsView, false);
            TextView outOfBundleType = (TextView) usageItemView.findViewById(R.id.textViewOutOfBundleType);

            outOfBundleType.setText(outOfBundle.getItemName() + " - Since " + outOfBundle.getOutOfBundleDateStr() +
                    ", " + outOfBundle.getUsageBandWidthStr() + " cost you " + outOfBundle.getCostStr());

            LinearLayout.LayoutParams layoutParamsUsageItem = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            outOfBundleItemsView.addView(usageItemView, layoutParamsUsageItem);
        }
        addView(view, layoutParams);
    }

    public OutOfBundleLayout(Context context) {
        super(context);
    }

    public OutOfBundleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}