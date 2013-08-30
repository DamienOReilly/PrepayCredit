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

package damo.three.ie.prepayusage;

import damo.three.ie.prepayusage.items.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class ItemFactory {
    /**
     * Our usage factory.
     *
     * @param item {@link JSONObject} usage in JSON object form
     * @return {@link BaseItem} usage object POJO
     * @throws ParseException
     * @throws JSONException
     */
    public static BaseItem createItem(JSONObject item) throws ParseException,
            JSONException {

        String type = item.getString("item");
        BaseItem baseItem;
        InternetUsageRegistry internetUsageRegistry = InternetUsageRegistry.getInstance();

        if (type.equals("Free internet allowance")) {
            baseItem = new Data(item.getString("value1"), item.getString("value2"));
        } else if (type.equals("Free Texts")) {
            baseItem = new Texts(item.getString("value1"), item.getString("value2"));
        } else if (type.equals("Talk and Text weekend minutes")) {
            baseItem = new WeekendVoiceMinutes(item.getString("value1"),
                    item.getString("value2"));
        } else if (type.equals("Free cash")) {
            baseItem = new FreeCash(item.getString("value1"),
                    item.getString("value2"));
        } else if (type.equals("Internet Add-on")) {
            baseItem = new InternetAddon(item.getString("value1"),
                    item.getString("value2"));
        } else if (type.equals("Internet")) {
            baseItem = new OutOfBundle(item.getString("value1"),
                    item.getString("value2"), item.getString("value3"));
        } else if (type.equals("Skype Calls")) {
            baseItem = new SkypeCalls(item.getString("value1"),
                    item.getString("value2"));
        } else if (type.equals("3 to 3 Calls")) {
            baseItem = new Three2ThreeCalls(item.getString("value1"),
                    item.getString("value2"));
        } else if (type.equals("Top-up")) {
            baseItem = new TopUp(item.getString("value1"), item.getString("value2"));
        } else {
            baseItem = new Other(item.getString("value1"), item.getString("value2"));
        }
        return baseItem;
    }

}
