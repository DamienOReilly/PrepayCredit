/*
 * This file is part of Prepay Credit for Android
 *
 * Copyright © 2013  Damien O'Reilly
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

import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.TypefaceSpan;
import org.xml.sax.XMLReader;

import java.util.ArrayList;

/**
 * Android Html.fromHtml(String) doesn't not support some tags that I'm using e.g. li ul.
 * By using code from http://blog.mohammedlakkadshaw.com/Handling_Custom_Tags_Using_Html.tagHandler().html
 * by Mohammed Lakkadshaw, we have support for these tags, when we use:
 * Html.fromHtml(String, null, new CustomTagHandler())
 * <p/>
 * I just changed the code to use ArrayList instead of the obsolete Vector and made
 * use of generics to overcome compiler unchecked warnings.
 */
public class CustomTagHandler implements Html.TagHandler {
    private final ArrayList<String> mListParents = new ArrayList<String>();
    private int mListItemCount = 0;

    @Override
    public void handleTag(final boolean opening, final String tag, Editable output, final XMLReader xmlReader) {

        if (tag.equals("ul") || tag.equals("ol") || tag.equals("dd")) {
            if (opening) {
                mListParents.add(tag);
            } else mListParents.remove(tag);

            mListItemCount = 0;
        } else if (tag.equals("li") && !opening) {
            handleListTag(output);
        } else if (tag.equalsIgnoreCase("code")) {
            if (opening) {
                output.setSpan(new TypefaceSpan("monospace"), output.length(), output.length(),
                        Spannable.SPAN_MARK_MARK);
            } else {
                Object obj = getLast(output, TypefaceSpan.class);
                int where = output.getSpanStart(obj);

                output.setSpan(new TypefaceSpan("monospace"), where, output.length(), 0);
            }
        }
    }

    private <T> Object getLast(Editable text, Class<T> kind) {
        Object[] objs = text.getSpans(0, text.length(), kind);
        if (objs.length == 0) {
            return null;
        } else {
            for (int i = objs.length; i > 0; i--) {
                if (text.getSpanFlags(objs[i - 1]) == Spannable.SPAN_MARK_MARK) {
                    return objs[i - 1];
                }
            }
            return null;
        }
    }

    private void handleListTag(Editable output) {
        if (mListParents.get(mListParents.size() - 1).equals("ul")) {
            output.append("\n");
            String[] split = output.toString().split("\n");

            int lastIndex = split.length - 1;
            int start = output.length() - split[lastIndex].length() - 1;
            output.setSpan(new BulletSpan(15 * mListParents.size()), start, output.length(), 0);
        } else if (mListParents.get(mListParents.size() - 1).equals("ol")) {
            mListItemCount++;

            output.append("\n");
            String[] split = output.toString().split("\n");

            int lastIndex = split.length - 1;
            int start = output.length() - split[lastIndex].length() - 1;
            output.insert(start, mListItemCount + ". ");
            output.setSpan(new LeadingMarginSpan.Standard(15 * mListParents.size()), start, output.length(), 0);
        }
    }
}