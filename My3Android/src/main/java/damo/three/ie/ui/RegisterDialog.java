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

package damo.three.ie.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockDialogFragment;
import damo.three.ie.R;

public class RegisterDialog extends SherlockDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Context context = getSherlockActivity();

        // 3 needs you to use your SIM's ICCID to register on My3. This is on
        // the back of the SIM card. So help the user get the ICCID without
        // having to take out the sim card, we will try read it for them.
        TelephonyManager tm = (TelephonyManager) getSherlockActivity()
                .getSystemService(Context.TELEPHONY_SERVICE);
        String iccid = tm.getSimSerialNumber();

        final TextView message = new TextView(context);

        // TODO: should move to strings.xml but...meh!
        String msg = "Please enter your 3 mobile number and password in settings. If you need to "
                + "register an account, you can do so here:<br><a href=https://my3account.three.ie/"
                + "Sign_up>my3account.three.ie/Sign_up</a><p><b>Note: </b>Access this URL over Wi-Fi or from a Desktop/Laptop. Accessing it while on 3's network will skip/bypass the register screen.<p>You need the last 6 digits of SIM-card "
                + "serial number to register.<p>";
        // Check if ICCID is not null and 6 in length. Devices without SIM won't
        // have ICCID.
        if (iccid != null && (iccid.length() >= 6)) {
            msg += " SIM Serial last 6 digits:&nbsp;<b>"
                    + iccid.substring(iccid.length() - 6) + "</b>";
        } else {
            msg += "Cannot read the SIM-card serial number! Have you a SIM-card inserted and PIN code "
                    + "entered? Alternatively, you can read the last 6 digits of SIM-card serial "
                    + "number yourself. (Take out battery and read the last 6 digits on SIM-card)";
        }

        message.setMovementMethod(LinkMovementMethod.getInstance());
        message.setText(Html.fromHtml(msg));

        return new AlertDialog.Builder(context)
                .setTitle(R.string.login_dialog_title).setCancelable(true)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.login_dialog_close, null)
                .setView(message)
                .setInverseBackgroundForced(Build.VERSION.SDK_INT < 11) // HONEYCOMB
                .create();
        // only inverse dialog on pre-honeycomb devices as Dialog with Sherlock
        // looks crappy on 2.3 and below
    }

}
