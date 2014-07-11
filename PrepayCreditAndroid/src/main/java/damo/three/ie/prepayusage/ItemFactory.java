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
import damo.three.ie.util.PrepayException;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Our usage factory.
 */
public class ItemFactory {

    public static UsageItem createItem(JSONObject item) throws ParseException, JSONException, PrepayException {

        String type = item.getString("item");
        UsageItem usage;

        if (type.equals("Free internet allowance")) {
            usage = new Data(item.getString("value1"), item.getString("value2"));
        } else if (type.equals("Free Texts")) {
            usage = new Texts(item.getString("value1"), item.getString("value2"));
        } else if (type.equals("Talk and Text weekend minutes")) {
            usage = new WeekendVoiceMinutes(item.getString("value1"), item.getString("value2"));
        } else if (type.equals("Free cash")) {
            usage = new FreeCash(item.getString("value1"), item.getString("value2"));
        } else if (type.equals("Internet Add-on")) {
            usage = new InternetAddon(item.getString("value1"), item.getString("value2"));
        } else if (type.startsWith("Internet")) {
            usage = new OutOfBundle(item.getString("item"), item.getString("value1"), item.getString("value2"),
                    item.getString("value3"));
        } else if (type.equals("Skype Calls")) {
            usage = new SkypeCalls(item.getString("value1"), item.getString("value2"));
        } else if (type.equals("3 to 3 Calls")) {
            usage = new Three2ThreeCalls(item.getString("value1"), item.getString("value2"));
        } else if (type.equals("Top-up")) {
            usage = new TopUp(item.getString("value1"), item.getString("value2"));
        } else {
            throw new PrepayException("Unknown item: " + item.toString());
        }
        return usage;
    }
}
