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

package damo.three.ie.util;

import damo.three.ie.prepayusage.ItemFactory;
import damo.three.ie.prepayusage.UsageItem;
import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class JSONUtils {

    /**
     * Convert our usages in JSON string representation to objects.
     *
     * @param jsonStringArray Usages in JSON string format
     * @return {@link List<damo.three.ie.prepayusage.UsageItem>}
     */
    public static List<UsageItem> jsonToUsageItems(String jsonStringArray) {
        List<UsageItem> usageItems = null;
        try {
            usageItems = jsonToUsageItems(new JSONArray(jsonStringArray));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return usageItems;
    }

    /**
     * Convert our usages in JSON representation to objects.
     *
     * @param jsonArray Usages in JSON format
     * @return {@link List<damo.three.ie.prepayusage.UsageItem>}
     */
    public static List<UsageItem> jsonToUsageItems(JSONArray jsonArray) {

        List<UsageItem> usageItems = new ArrayList<UsageItem>();

        if (jsonArray != null) {
            boolean problemOccurred = false;
            Exception ex = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item;
                try {
                    item = jsonArray.getJSONObject(i);
                    usageItems.add(ItemFactory.createItem(item));
                } catch (JSONException e) {
                    problemOccurred = true;
                    ex = e;
                } catch (ParseException e) {
                    problemOccurred = true;
                    ex = e;
                } catch (PrepayException e) {
                    problemOccurred = true;
                    ex = e;
                }
            }
            if (problemOccurred) {
                // Unknown usage items. Log report for possible bug.
                ACRA.getErrorReporter().putCustomData("JSON_ITEMS", jsonArray.toString());
                ACRA.getErrorReporter().handleSilentException(ex);
            }
        }
        return usageItems;
    }

}