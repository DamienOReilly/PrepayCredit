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

import damo.three.ie.prepayusage.UsageItem;
import damo.three.ie.util.DateUtils;
import damo.three.ie.util.NumberUtils;

import java.text.ParseException;

public class OutOfBundle extends UsageItem {

    private Number usageBandwidth;
    private Number cost;
    private Number outOfBundleDate;

    public OutOfBundle(String type, String usageBandwidth, String cost, String outOfBundleDate) throws ParseException {
        super(type);
        setUsageBandWidth(usageBandwidth);
        setCost(cost);
        setOutOfBundleDate(outOfBundleDate);
    }

    void setUsageBandWidth(String usageBandwidth) throws ParseException {
        this.usageBandwidth = NumberUtils.parseNumeric(usageBandwidth);
    }

    public String getUsageBandWidthStr() {
        return NumberUtils.formatFloat(usageBandwidth) + "MB";
    }

    void setCost(String cost) throws ParseException {
        this.cost = NumberUtils.parseMoney(cost);
    }

    public String getCostStr() {
        return NumberUtils.formatMoney(cost);
    }

    void setOutOfBundleDate(String outOfBundleDate) {
        this.outOfBundleDate = DateUtils.parseOutOfBundleDate(outOfBundleDate);
    }

    public String getOutOfBundleDateStr() {
        return DateUtils.formatDate(outOfBundleDate);
    }

}