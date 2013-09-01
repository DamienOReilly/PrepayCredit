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

package damo.three.ie.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragment;
import damo.three.ie.prepay.AccountProcessor;
import damo.three.ie.prepay.UsageNotifier;
import damo.three.ie.prepayusage.BaseItem;
import damo.three.ie.prepayusage.InternetUsageRegistry;
import damo.three.ie.prepayusage.items.Data;
import damo.three.ie.prepayusage.items.InternetAddon;
import damo.three.ie.util.JSONUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.List;

public class AccountProcessorFragment extends SherlockFragment {

    private Boolean working = false;
    private List<BaseItem> items = null;
    private DateTime dateTimeNow = null;
    private SharedPreferences sharedPref = null;

    /**
     * Callback interface through which the fragment will report the task's
     * progress and results back to the Activity.
     */
    public interface AccountProcessorListener {
        void onAccountUsageReceived();

        void onAccountUsageExceptionReceived(String exception);
    }

    private AccountProcessorListener accountProcessorListener;

    /**
     * Hold a reference to the parent Activity so we can report the task's
     * current progress and results. The Android framework will pass us a
     * reference to the newly created Activity after each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof AccountProcessorListener)) {
            throw new IllegalStateException(
                    "Activity must implement AccountProcessorListener");
        }

        accountProcessorListener = (AccountProcessorListener) activity;
        sharedPref = getSherlockActivity().getApplicationContext().getSharedPreferences(
                "damo.three.ie.previous_usage", Context.MODE_PRIVATE);
    }

    /**
     * Set the callback to null so we don't accidentally leak the Activity
     * instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();

        /* prevents us holding onto an activity which has been destroyed. Should stop potential memory leak */
        accountProcessorListener = null;
        sharedPref = null;
    }

    /**
     * This method will only be called once when the retained Fragment is first
     * created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Kick off the Usage fetcher.
     *
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     */
    public void execute() throws
            KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException {
        working = true;
        items = null;
        AccountProcessor accountProcessor = new AccountProcessor(this);
        accountProcessor.execute();

    }

    /**
     * Pass back exception to {@link damo.three.ie.activity.PrepayCreditActivity}
     *
     * @param damn The exception to pass back
     */
    public void reportBackException(Throwable damn) {
        working = false;
        if (accountProcessorListener != null) {
            accountProcessorListener.onAccountUsageExceptionReceived(damn
                    .getLocalizedMessage());
        }

    }

    /**
     * Pass back exception to {@link damo.three.ie.activity.PrepayCreditActivity}
     *
     * @param usages Usages retrieved
     */
    public void reportBackUsages(JSONArray usages) {
        working = false;
        try {
            /**
             *  We got usages back successfully, so clear out usages from InternetUsageRegistry
             *  before creating POJO's, as these may or may not add new entries.
             */
            if (usages.length() > 0) {
                InternetUsageRegistry.getInstance().clear();
            }
            items = JSONUtils.jsonToBaseItems(usages);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dateTimeNow = new DateTime();

        boolean notificationsEnabled = false;
        if (sharedPref != null) {
            notificationsEnabled = sharedPref.getBoolean("notification", true);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong("last_refreshed_milliseconds", dateTimeNow.getMillis());
            editor.putString("usage_info", usages.toString());
            editor.commit();
        }

        if (accountProcessorListener != null) {
            accountProcessorListener.onAccountUsageReceived();
            registerAlarm(notificationsEnabled);
        }
    }

    /**
     * Register alarm when last internet data/add-on expires.
     * Clear alarms if they are no longer relevant.
     * TODO: Is this the best place to really do this?
     */
    private void registerAlarm(boolean notificationsEnabled) {
        InternetUsageRegistry internetUsageRegistry = InternetUsageRegistry.getInstance();

        /* For each Internet add-on, add it to InternetUsageRegistry */
        for (BaseItem b : items) {
            if (b instanceof InternetAddon || b instanceof Data) {
                /* Make sure it's not expired first */
                if (b.isNotExpired()) {
                    internetUsageRegistry.submit(b.getValue1().longValue());
                }
            }
        }

        AlarmManager am = (AlarmManager) getSherlockActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getSherlockActivity().getApplicationContext(), UsageNotifier.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getSherlockActivity().getApplicationContext(), 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        if (internetUsageRegistry.getDateTime() != null && notificationsEnabled) {
            am.cancel(pendingIntent);

            /**
             *  Set one-time alarm. 4 hours enough of a warning ?
             *  Also, don't bother setting an alarm if they check usage within 4 hours of expiring
             *  Otherwise they will get immediate alarm triggers every time they refresh.
             */
            DateTime internetExpires = internetUsageRegistry.getDateTime().minusHours(4);
            if (new DateTime().compareTo(internetExpires) < 0) {
                am.set(AlarmManager.RTC_WAKEUP, internetExpires.getMillis(),
                        pendingIntent);
            }
        } else {
            am.cancel(pendingIntent);
        }
    }


    /**
     * Provide a method to the activity to know if the ASync task is still working
     * In-case the Activity was re-created.
     *
     * @return Are we currently fetching usage
     */
    public Boolean isWorking() {
        return working;
    }

    /**
     * @return Usages
     */
    public List<BaseItem> getItems() {
        return items;
    }

    /**
     * @return DateTime the usages were fetched
     */
    public DateTime getDateTime() {
        return dateTimeNow;
    }

}