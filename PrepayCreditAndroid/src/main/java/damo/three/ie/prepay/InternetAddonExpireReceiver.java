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

package damo.three.ie.prepay;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import damo.three.ie.R;
import damo.three.ie.activity.InternetExpirationActivity;
import damo.three.ie.prepayusage.InternetUsageRegistry;
import org.joda.time.DateTime;

/**
 * Adds a notification in the notification bar when an internet add-on has expired or is about to.
 */
public class InternetAddonExpireReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        long millis = intent.getExtras().getLong(InternetUsageRegistry.INTERNET_EXPIRE_TIME);

        boolean alreadyExpired = false;

        if (millis > 0) {
            long now = new DateTime().getMillis();
            if (now > millis) {
                alreadyExpired = true;
            }
        }

        // Clicking on the notification opens the InternetExpirationActivity.
        Intent myIntent = new Intent(context, InternetExpirationActivity.class);
        myIntent.putExtra(InternetUsageRegistry.INTERNET_EXPIRED, alreadyExpired);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, myIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_warning)
                .setLargeIcon(largeIcon)
                .setContentTitle(context.getString(alreadyExpired ? R.string.internet_addon_expired_title : R.string
                        .internet_addon_expiring_title))
                .setTicker(context.getString(alreadyExpired ? R.string.internet_addon_expired_title : R.string
                        .internet_addon_expiring_title))
                .setContentText(context.getString(alreadyExpired ? R.string.internet_addon_expired_text : R.string
                        .internet_addon_expiring_text))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, mBuilder.build());
    }
}
