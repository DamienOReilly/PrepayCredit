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

package damo.three.ie.prepayusage.items;

import damo.three.ie.prepayusage.BasicUsageItem;
import damo.three.ie.util.NumberUtils;

import java.text.ParseException;

public class FreeCash extends BasicUsageItem {

    public FreeCash(String value1str, String value2str) throws ParseException {
        super("Free Cash");
        setValue1(value1str);
        setValue2(value2str);
    }

    @Override
    public String getValue2formatted() {
        return NumberUtils.formatMoney(value2);
    }

    @Override
    public void setValue2(String value2str) throws ParseException {
        value2 = NumberUtils.parseMoney(value2str);
    }

}