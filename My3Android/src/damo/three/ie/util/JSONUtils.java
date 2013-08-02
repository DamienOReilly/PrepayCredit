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

package damo.three.ie.util;

import damo.three.ie.my3usage.BaseItem;
import damo.three.ie.my3usage.ItemFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class JSONUtils {

    /**
     * Convert our usages in JSON representation to BaseItem objects for further
     * processing.
     *
     * @param jsonArray Usages in JSON format
     * @return {@link List<BaseItem>}
     * @throws JSONException
     * @throws ParseException
     */
    public static List<BaseItem> jsonToBaseItems(JSONArray jsonArray)
            throws ParseException, JSONException {

        List<BaseItem> usageItems = new ArrayList<BaseItem>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                usageItems.add(new ItemFactory().createItem(item));
            }
        }

        return usageItems;
    }

}