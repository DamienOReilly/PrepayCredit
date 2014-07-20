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
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import damo.three.ie.prepay.UpdateAsyncTask;
import damo.three.ie.prepayusage.BasicUsageItem;
import damo.three.ie.prepayusage.UsageItem;
import damo.three.ie.util.JSONUtils;
import damo.three.ie.util.UsageUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;

import java.util.List;

/**
 * Fragment which deals with initiating the fetching of users usages and reporting it back to the PrepayCreditActivity.
 */
public class UpdateFragment extends Fragment {

    private Boolean working = false;
    private List<UsageItem> items = null;
    private DateTime dateTimeNow = null;
    private SharedPreferences sharedPref = null;
    private SharedPreferences usageSharedPref = null;
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
            throw new IllegalStateException("Activity must implement AccountProcessorListener");
        }

        accountProcessorListener = (AccountProcessorListener) activity;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        usageSharedPref = getActivity().getApplicationContext().getSharedPreferences("damo.three.ie.previous_usage",
                Context.MODE_PRIVATE);
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
     */
    public void execute() {
        working = true;
        items = null;
        UpdateAsyncTask updateAsyncTask = new UpdateAsyncTask(this);
        updateAsyncTask.execute();
    }

    /**
     * Pass back exception to {@link damo.three.ie.activity.PrepayCreditActivity}
     *
     * @param exception The exception to pass back
     */
    public void reportBackException(Exception exception) {
        working = false;
        if (accountProcessorListener != null) {
            accountProcessorListener.onAccountUsageExceptionReceived(exception);
        }
    }

    /**
     * Pass back exception to {@link damo.three.ie.activity.PrepayCreditActivity}
     *
     * @param usages Usages retrieved
     */
    public void reportBackUsages(JSONArray usages) {
        working = false;
        items = JSONUtils.jsonToUsageItems(usages);

        dateTimeNow = new DateTime();

        boolean notificationsEnabled = sharedPref.getBoolean("notification", true);
        SharedPreferences.Editor editor = usageSharedPref.edit();
        editor.putLong("last_refreshed_milliseconds", dateTimeNow.getMillis());
        editor.putString("usage_info", usages.toString());
        editor.commit();

        /**
         * Check if reference to parent activity is null, if it is, app was closed,
         * and Activity was not re-created. i.e. user manually closed the app while fetching
         * data
         **/
        if (accountProcessorListener != null) {
            accountProcessorListener.onAccountUsageReceived();
            List<BasicUsageItem> basicUsageItems = UsageUtils.getAllBasicItems(items);
            UsageUtils.registerInternetExpireAlarm(getActivity().getApplicationContext(), basicUsageItems,
                    notificationsEnabled, true);
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
    public List<UsageItem> getItems() {
        return items;
    }

    /**
     * @return DateTime the usages were fetched
     */
    public DateTime getDateTime() {
        return dateTimeNow;
    }

    /**
     * Callback interface through which the fragment will report the task's
     * progress and results back to the Activity.
     */
    public interface AccountProcessorListener {
        void onAccountUsageReceived();

        void onAccountUsageExceptionReceived(Exception exception);
    }

}