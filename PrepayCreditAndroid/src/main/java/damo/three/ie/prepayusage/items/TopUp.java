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
import java.util.List;

public class TopUp extends BasicUsageItem {

    private float quantity;

    public TopUp(String dateExpireStr, String quantityStr) throws ParseException {
        super("Top-Up");
        setExpireDate(dateExpireStr);
        setQuantityFormatted(quantityStr);
    }

    @Override
    public String getQuantityFormatted() {
        return getQuantityFormatted(quantity);
    }

    @Override
    public void setQuantityFormatted(String quantityStr) throws ParseException {
        quantity = NumberUtils.parseMoney(quantityStr).floatValue();
    }

    @Override
    public Number getQuantity() {
        return quantity;
    }

    private String getQuantityFormatted(float i) {
        return NumberUtils.formatMoney(i);
    }

    @Override
    public String mergeQuantity(List<Number> toSum) {
        float i = 0;
        for (Number a : toSum) {
            i += a.floatValue();
        }

        return getQuantityFormatted(i);
    }

}