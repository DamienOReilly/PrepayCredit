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

package damo.three.ie.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import damo.three.ie.R;
import damo.three.ie.fragment.UpdateFragment;
import damo.three.ie.net.ThreeHttpClient;
import damo.three.ie.prepay.Constants;
import damo.three.ie.prepayusage.BasicUsageItem;
import damo.three.ie.prepayusage.BasicUsageItemsGrouped;
import damo.three.ie.prepayusage.UsageItem;
import damo.three.ie.prepayusage.items.OutOfBundle;
import damo.three.ie.ui.BasicUsageLayout;
import damo.three.ie.ui.ExtendedScrollView;
import damo.three.ie.ui.OutOfBundleLayout;
import damo.three.ie.util.DateUtils;
import damo.three.ie.util.JSONUtils;
import damo.three.ie.util.PrepayException;
import damo.three.ie.util.UsageUtils;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.List;

public class PrepayCreditActivity extends ActionBarActivity implements
        UpdateFragment.AccountProcessorListener {

    private boolean working = false;
    private boolean refreshedOnStart = false;
    private boolean refreshDoneSinceLoadingPersistedData = false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences usageSharedPreferences;

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout baseUsageView;
    private RelativeLayout errorLayout;
    private ExtendedScrollView scrollView;
    private UpdateFragment updateFragment;
    private TextView lastRefreshed;

    /**
     * Called when the activity is first created or re-created due to configuration change. e.g. device rotation
     *
     * @param savedInstanceState Saved Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            refreshedOnStart = savedInstanceState.getBoolean("refreshed_on_start", false);
            refreshDoneSinceLoadingPersistedData = savedInstanceState.getBoolean("loaded_persisted_on_start", false);
        }

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        usageSharedPreferences = getSharedPreferences("damo.three.ie.previous_usage", Context.MODE_PRIVATE);

        // Register or clear background update alarms depending if they are enabled or not.
        boolean backgroundUpdate = sharedPreferences.getBoolean(getString(R.string.backgroundupdate), true);
        UsageUtils.setupBackgroundUpdateAlarms(getApplicationContext(), backgroundUpdate);

        ActionBar actionBar = getSupportActionBar();
        actionBar.show();

        setContentView(R.layout.main_usage_layout);
        lastRefreshed = (TextView) findViewById(R.id.textview_last_refreshed);
        lastRefreshed.setOnClickListener(new OnLastRefreshedTextViewClickListener());

        errorLayout = (RelativeLayout) findViewById(R.id.error_layout);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new OnSwipeRefreshLayoutListener());
        swipeRefreshLayout.setColorScheme(R.color.holo_purple, R.color.holo_green_light, R.color.holo_orange_light,
                R.color.holo_red_light);

        scrollView = (ExtendedScrollView) findViewById(R.id.usage_scroll_view);
        scrollView.setOnScrollViewListener(new ExtendedScrollView.OnScrollViewListener() {
            @Override
            public void onScrollChanged(ExtendedScrollView v, int l, int t, int oldl, int oldt) {
                View view = scrollView.getChildAt(0);
                // If we are at top of scrollview, enable the swipe refresh layout again, else keep disabled to prevent
                // scrolling the scrollview triggering a refresh.
                if (view.getTop() == scrollView.getScrollY()) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

        baseUsageView = (LinearLayout) findViewById(R.id.usage_layout);

        // maybe user rotated the device and fragment already exists?
        FragmentManager fm = getSupportFragmentManager();
        updateFragment = (UpdateFragment) fm.findFragmentByTag("usage_fetcher");

        if (updateFragment == null) {
            updateFragment = new UpdateFragment();
            // consider that activity may be destroyed
            fm.beginTransaction().add(updateFragment, "usage_fetcher").commitAllowingStateLoss();
        }

        // if we had already fetched usages, show them on the newly created activity
        if (updateFragment.getItems() != null) {
            displayUsages(updateFragment.getItems());
            updateLastRefreshedTextView(new PrettyTime().format(new Date((updateFragment.getDateTime().getMillis()))));
        }
        /**
         * if screen was rotated and Activity was re-created while we were fetching usage info, then enable the swipe
         * refresh animation.
         */
        if (updateFragment.isWorking()) {
            swipeRefreshLayout.setRefreshing(true);
            working = true;
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        /**
         * If previous usage info was persisted, show it. If we refreshed usages since opening app, then don't fall in
         * here as we will get the usages from our AccountProcessorFragment in onCreate() instead.
         */
        if (!refreshDoneSinceLoadingPersistedData) {
            loadPersistedUsages();
        }

        /**
         * refresh usage on startup ?
         * Check if we already refreshed. Activity is re-created each rotate, so checked persisted value we stored
         * onSaveInstanceState()
         */
        if ((sharedPreferences.getBoolean("refresh", false)) && (!refreshedOnStart)) {
            getCreditInfo();
            refreshedOnStart = true;
        }
    }

    /**
     * Load usages if we have them persisted.
     */
    private void loadPersistedUsages() {
        String usage = usageSharedPreferences.getString("usage_info", null);
        // first check if anything was persisted
        if (usage != null) {
            List<UsageItem> usageItems = JSONUtils.jsonToUsageItems(usage);
            // check array size in-case it was just an empty json string stored
            if (usageItems != null && usageItems.size() > 0) {
                updateLastRefreshedTextView(new PrettyTime().format(new Date((usageSharedPreferences.getLong
                        ("last_refreshed_milliseconds", 0L)))));
                displayUsages(usageItems);
            }
        }
    }

    /**
     * Update the LastRefreshed TextView
     *
     * @param last String representation of a date when a last refresh was performed
     */
    private void updateLastRefreshedTextView(String last) {
        // non line breaking space appended to the end to prevent last italic char been clipped
        lastRefreshed.setText("Last refreshed " + last + "\u00A0");
        lastRefreshed.setVisibility(View.VISIBLE);
    }

    /**
     * Activity is going down. Save data that we want to reload when the activity is re-created.
     *
     * @param outState State to be saved
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("refreshed_on_start", refreshedOnStart);
        // signal to possibly re-load on rotate as onResume() is called each configuration change
        outState.putBoolean("loaded_persisted_on_start", false);
    }

    /**
     * Setup the action icon's for the ActionBar.
     *
     * @param menu Menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_refresh:
                if (!working) {
                    getCreditInfo();
                }
                return true;

            case R.id.menu_settings:
                /**
                 * Needed as once off. If user enables refresh on start, refresh
                 * would happen when they close SettingsActivity as onResume() here
                 * is called as soon as user closes SettingsActivity
                 */
                //refreshedOnStart = true;
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                return true;

            case R.id.menu_about:
                Intent about = new Intent(this, AboutActivity.class);
                startActivity(about);
                return true;

            case R.id.menu_my3_website:
                goToMy3Website();
                return true;

            case R.id.menu_logout:
                logOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Clear credentials, stored usage info, cookies and go back to login screen.
    private void logOut() {
        SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();
        sharedPrefsEditor.remove("mobile");
        sharedPrefsEditor.remove("password");
        sharedPrefsEditor.commit();

        SharedPreferences.Editor usageSharedPrefsEditor = usageSharedPreferences.edit();
        usageSharedPrefsEditor.remove("last_refreshed_milliseconds");
        usageSharedPrefsEditor.remove("usage_info");
        usageSharedPrefsEditor.commit();

        try {
            ThreeHttpClient.getInstance(getApplicationContext()).getHttpClient().getCookieStore().clear();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void goToMy3Website() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(Constants.MY3_MAIN_PAGE));
        startActivity(i);
    }

    /**
     * Initiate the request to get users usage information
     */
    private void getCreditInfo() {
        if (sharedPreferences.getString("mobile", "").equals("") ||
                sharedPreferences.getString("password", "").equals("")) {
            showWarning(getString(R.string.no_account_credentials));
        } else {
            working = true;
            clearErrorLayout();
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            updateFragment.execute();
        }
    }

    /**
     * Callback function to handle when usage has been retrieved and parsed
     */
    @Override
    public void onAccountUsageReceived() {

        List<UsageItem> usageItems = updateFragment.getItems();

        if (usageItems != null) {
            updateLastRefreshedTextView(new PrettyTime().format(new Date(updateFragment.getDateTime().getMillis())));
            displayUsages(usageItems);
        }
        swipeRefreshLayout.setRefreshing(false);
        working = false;
    }

    /**
     * Exception callback receiver
     *
     * @param exception Exception from fetching usages
     */
    @Override
    public void onAccountUsageExceptionReceived(Exception exception) {
        working = false;
        swipeRefreshLayout.setRefreshing(false);
        if ((exception instanceof IOException) || (exception instanceof PrepayException)) {
            showCriticalError(exception);
        } else {
            showWarning(exception);
        }
    }

    /**
     * Show an message. Message shows until user exits it, or refreshes again.
     */
    private void showCriticalError(Exception exception) {
        String msg = String.format(getString(R.string.my3_connection_error), exception.getLocalizedMessage());
        setupErrorLayout(msg, new OnErrorCloseClickListener(), View.VISIBLE, new OnErrorLayoutClickListener(), 0);
    }

    /**
     * Setup an error layout based on supplied criteria.
     *
     * @param msg                        Message to show.
     * @param imgButtonOnClickListener   OnClickListener when user clicks X button. Supply null for no action.
     * @param imgButtonVisible           Close button is visible or not.
     * @param errorLayoutOnClickListener OnClickListener when user clicks layout. Supply null for no action.
     * @param duration                   Duration (in seconds) for the layout to retain on screen before disappearing.
     *                                   Use 0 to disable.
     */
    private void setupErrorLayout(String msg, View.OnClickListener imgButtonOnClickListener, int imgButtonVisible,
                                  View.OnClickListener errorLayoutOnClickListener, int duration) {
        TextView errorTextView = (TextView) findViewById(R.id.error_text);
        errorTextView.setText(msg);
        ImageButton imageButton = (ImageButton) errorLayout.findViewById(R.id.error_close_button);
        imageButton.setOnClickListener(imgButtonOnClickListener);
        imageButton.setVisibility(imgButtonVisible);
        errorLayout.setOnClickListener(errorLayoutOnClickListener);
        errorLayout.setVisibility(View.VISIBLE);

        if (duration > 0) {
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    clearErrorLayout();
                }
            };
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 5 * 1000);
        }
    }

    /**
     * Show a warning message. Behaviour is to disappear after a few seconds.
     */
    private void showWarning(String msg) {
        setupErrorLayout(msg, null, View.INVISIBLE, null, 5);
    }

    private void showWarning(Exception exception) {
        showWarning(exception.getLocalizedMessage());
    }

    private void clearErrorLayout() {
        errorLayout.setVisibility(View.GONE);
    }

    /**
     * Display the users usages.
     *
     * @param usageItems Usages Retrieved
     */
    private void displayUsages(List<UsageItem> usageItems) {
        if (usageItems != null) {

            baseUsageView.removeAllViews();

            // Out of bundle items
            List<OutOfBundle> outOfBundleItems = UsageUtils.getAllOutOfBundleItems(usageItems);
            if (outOfBundleItems.size() > 0) {
                OutOfBundleLayout outOfBundleLayout = new OutOfBundleLayout(this, outOfBundleItems);
                baseUsageView.addView(outOfBundleLayout);
            }

            // Basic usage items
            List<BasicUsageItem> basicUsageItems = UsageUtils.getAllBasicItems(usageItems);
            List<BasicUsageItemsGrouped> basicUsageItemsGrouped = UsageUtils.groupUsages(basicUsageItems);
            for (BasicUsageItemsGrouped b : basicUsageItemsGrouped) {
                /**
                 * check if usage is already expired (cached usages maybe no-longer relevant if the user hasn't
                 * refreshed in some time).
                 */
                if (b.isNotExpired()) {
                    BasicUsageLayout l = new BasicUsageLayout(getBaseContext(), b);
                    baseUsageView.addView(l);
                }
            }
            refreshDoneSinceLoadingPersistedData = true;
        }
    }

    /**
     * Listener class. Action to perform when the error layout is clicked.
     */
    private class OnErrorLayoutClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            errorLayout.setVisibility(View.GONE);
            goToMy3Website();
        }
    }

    /**
     * Listener class. Action to perform when the user 'swipes' down to refresh.
     */
    private class OnSwipeRefreshLayoutListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            if (!working) {
                getCreditInfo();
            }
        }
    }

    /**
     * Listener class. Action to perform when the close button on error layout is clicked.
     */
    private class OnErrorCloseClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            errorLayout.setVisibility(View.GONE);
        }
    }

    private class OnLastRefreshedTextViewClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            long lastRefreshed = usageSharedPreferences.getLong("last_refreshed_milliseconds", 0L);
            if (lastRefreshed > 0) {
                Toast.makeText(getApplicationContext(), "Last refreshed on " + DateUtils.formatDateTime
                        (lastRefreshed), Toast.LENGTH_LONG).show();
            }
        }
    }
}
