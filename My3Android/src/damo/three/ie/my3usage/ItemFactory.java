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

package damo.three.ie.my3usage;

import damo.three.ie.my3usage.items.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class ItemFactory {

    public BaseItem createItem(JSONObject item) throws ParseException,
            JSONException {

        String type = item.getString("item");

        if (type.equals("Free internet allowance"))
            return new Data(item.getString("value1"), item.getString("value2"));
        else if (type.equals("Free Texts"))
            return new Texts(item.getString("value1"), item.getString("value2"));
        else if (type.equals("Talk and Text weekend minutes"))
            return new WeekendVoiceMinutes(item.getString("value1"),
                    item.getString("value2"));
        else if (type.equals("Free cash"))
            return new FreeCash(item.getString("value1"),
                    item.getString("value2"));
        else if (type.equals("Internet Add-on"))
            return new InternetAddon(item.getString("value1"),
                    item.getString("value2"));
        else if (type.equals("Internet")) {
            return new OutOfBundle(item.getString("value1"),
                    item.getString("value2"), item.getString("value3"));
        } else if (type.equals("Skype Calls"))
            return new SkypeCalls(item.getString("value1"),
                    item.getString("value2"));
        else if (type.equals("3 to 3 Calls"))
            return new Three2ThreeCalls(item.getString("value1"),
                    item.getString("value2"));
        else if (type.equals("Top-up"))
            return new TopUp(item.getString("value1"), item.getString("value2"));
        else
            return new Other(item.getString("value1"), item.getString("value2"));

    }

}
