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

package damo.three.ie;

import android.app.Application;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender.Method;
import org.acra.sender.HttpSender.Type;

// If you forked this project, you should create your own bug CouchDB host and credentials
// See https://github.com/ACRA/acra/wiki/BasicSetup and
// https://github.com/ACRA/acralyzer/wiki/setup for more information.
@ReportsCrashes(
        excludeMatchingSharedPreferencesKeys={"mobile","password"}, // Don't report the user's credentials
        formKey = "", // This is required for backward compatibility but not used
        formUri = "https://damienoreilly.cloudant.com/acra-my3prepay-bug-reports/_design/acra-storage/_update/report",
        reportType = Type.JSON,
        httpMethod = Method.PUT,
        socketTimeout = 60000,
        formUriBasicAuthLogin = "chimptivedsterediessedde",
        formUriBasicAuthPassword = "DWIlqwsptr3lT12jfqjebGQE",
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crash_toast_text,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogOkToast = R.string.crash_dialog_ok_toast

)
public class My3Application extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}
