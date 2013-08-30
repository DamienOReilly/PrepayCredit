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

package damo.three.ie.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockDialogFragment;
import damo.three.ie.R;

public class InfoDialog extends SherlockDialogFragment {

    private SharedPreferences sharedPreferences = null;

    private final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {

            SharedPreferences.Editor editor = sharedPreferences.edit();

            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    editor.putBoolean("firstrun", false);
                    editor.putBoolean("intermediate_server", true);
                    editor.commit();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    editor.putBoolean("firstrun", false);
                    editor.putBoolean("intermediate_server", false);
                    editor.commit();
                    break;
            }
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Context context = getSherlockActivity();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        final TextView message = new TextView(context);

        // TODO: should move to strings.xml but...meh!
        String msg = "This application will use an intermediate server to "
                + "speed up usage retrieval by default. This is most noticeable when "
                + "you are on a <b>2G</b> connection, and to some extend a <b>3G</b> "
                + "connection. The intermediate server is <b>secure.damienoreilly.org</b> "
                + "and traffic is encrypted over TLS. The intermediate "
                + "server is not used when you are on <b>Wi-Fi</b>.<p>Do you wish "
                + "to use this feature for 2G/3G, or would you rather "
                + "connect directly to my3account.three.ie?<p>(You can change "
                + "this option later in settings if you change your mind)";

        message.setMovementMethod(LinkMovementMethod.getInstance());
        message.setText(Html.fromHtml(msg));
        message.setTextColor(getResources().getColor(
                android.R.color.primary_text_light_nodisable));

        return new AlertDialog.Builder(context)
                .setTitle(R.string.firstrun_dialog_title)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.firstrun_dialog_yes,
                        dialogClickListener)
                .setNegativeButton(R.string.firstrun_dialog_no,
                        dialogClickListener).setView(message)
                .setInverseBackgroundForced(Build.VERSION.SDK_INT < 11) // HONEYCOMB
                .create();
                // only inverse dialog on pre-honeycomb devices as Dialog with Sherlock
                // looks crappy on 2.3 and below
    }

}
