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

package damo.three.ie.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Class to extend a {@link android.widget.ScrollView}'s functionality so we can detect when we have scrolled.
 * onScrollChanged() is protected in ScrollView. By extending ScrollView, we can then enable/disable the
 * {@link android.support.v4.widget.SwipeRefreshLayout} accordingly so the SwipeRefreshLayout doesn't get fired when
 * scrolling up through the usages before getting to the top.
 *
 * @author Damien O'Reilly
 */
public class ExtendedScrollView extends ScrollView {

    private OnScrollViewListener mOnScrollViewListener;

    public ExtendedScrollView(Context context) {
        super(context);
    }

    public ExtendedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        mOnScrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setOnScrollViewListener(OnScrollViewListener onScrollViewListener) {
        this.mOnScrollViewListener = onScrollViewListener;
    }

    public interface OnScrollViewListener {
        void onScrollChanged(ExtendedScrollView v, int l, int t, int oldl, int oldt);
    }
}
