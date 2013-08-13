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

package damo.three.ie.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import damo.three.ie.R;
import damo.three.ie.fragment.AccountProcessorFragment;
import damo.three.ie.my3usage.BaseItem;
import damo.three.ie.my3usage.BaseItemsGroupedAndSorted;
import damo.three.ie.my3usage.OrganiseItems;
import damo.three.ie.ui.InfoDialog;
import damo.three.ie.ui.RegisterDialog;
import damo.three.ie.ui.UpdatingView;
import damo.three.ie.ui.UsageView;
import damo.three.ie.util.JSONUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.List;

public class My3Activity extends SherlockFragmentActivity implements
        AccountProcessorFragment.AccountProcessorListener {

    private Boolean working = false;
    private Boolean refreshedOnStart = false;
    private Boolean refreshDoneSinceLoadingPersistedData = false;
    private SharedPreferences sharedPreferences = null;
    private UpdatingView updatingView = null;
    private RelativeLayout parentView = null;
    private LinearLayout baseUsageView = null;
    private ScrollView scrollView = null;
    private AccountProcessorFragment accountProcessorFragment = null;
    private TextView lastRefreshed = null;

    /**
     * Called when the activity is first created or re-created due to
     * configuration change. e.g. device rotation
     *
     * @param savedInstanceState Saved Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            refreshedOnStart = savedInstanceState.getBoolean(
                    "refreshed_on_start", false);
            refreshDoneSinceLoadingPersistedData = savedInstanceState
                    .getBoolean("loaded_persisted_on_start", false);

        }

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.main);
        parentView = (RelativeLayout) findViewById(R.id.main);
        lastRefreshed = (TextView) findViewById(R.id.textViewLastRefreshed);

        // maybe user rotated the device and fragment already exists?
        FragmentManager fm = getSupportFragmentManager();
        accountProcessorFragment = (AccountProcessorFragment) fm
                .findFragmentByTag("usage_fetcher");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.actionbar));
        actionBar.show();

        if (accountProcessorFragment == null) {
            accountProcessorFragment = new AccountProcessorFragment();
            fm.beginTransaction()
                    .add(accountProcessorFragment, "usage_fetcher").commit();
        }

        // if we had already fetched usages, show them on the newly created
        // activity
        if (accountProcessorFragment.getItems() != null) {
            displayUsages(accountProcessorFragment.getItems());
            updateLastRefreshedTextView(accountProcessorFragment.getDate());
        }

        // if screen was rotated and Activity was re-created while we were fetching
        // usage info, then display loading screen now.
        if (accountProcessorFragment.isWorking()) {
            displayLoadingView(true);
            working = true;
        } else {
            // create the view anyway, otherwise the loading screen never shows
            // after a rotation, but don't show it yet.
            displayLoadingView(false);
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        boolean firstrun = sharedPreferences.getBoolean("firstrun", true);
        if (firstrun) {
            showInfoDialog();
        }

        // If previous usage info was persisted, show it, unless we are
        // currently retrieving new usages.
        // If we refreshed usages since opening app, then don't fall in
        // here as we will get the usages from our AccountProcessorFragment
        // in onCreate() instead.
        if (!refreshDoneSinceLoadingPersistedData && !working) {
            loadPersistedUsages();
        }

        // refresh usage on startup ?
        // check if we already refreshed. Activity is re-created each
        // rotate, so checked persisted value we stored
        // onSaveInstanceState()
        if ((sharedPreferences.getBoolean("refresh", false))
                && (!refreshedOnStart)) {

            getCreditInfo();
            refreshedOnStart = true;
        }
    }

    private void loadPersistedUsages() {
        SharedPreferences sharedPref = getSharedPreferences(
                "damo.three.ie.previous_usage", Context.MODE_PRIVATE);
        String usage = sharedPref.getString("usage_info", null);
        // first check if anything was persisted
        if (usage != null) {
            try {
                List<BaseItem> baseItems = JSONUtils
                        .jsonToBaseItems(new JSONArray(usage));
                // check array size incase it was just an empty json
                // string stored
                if (baseItems.size() > 0) {
                    updateLastRefreshedTextView(sharedPref.getString(
                            "last_refreshed", "unknown"));
                    displayUsages(baseItems);

                }

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
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
        lastRefreshed.setText("Last refreshed: " + last + "\u00A0");
        lastRefreshed.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("refreshed_on_start", refreshedOnStart);
        // signal to possibly re-load on rotate as onResume() is called each
        // configuration change
        outState.putBoolean("loaded_persisted_on_start", false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuRefresh:
                if (!working) {
                    getCreditInfo();
                }
                return true;

            case R.id.menuSettings:
                // Needed as once off. If user enables refresh on start, refresh
                // would happen when they close SettingsActivity as onResume() here
                // is called as son as user closes SettingsActivity
                refreshedOnStart = true;
                Intent intent = new Intent(My3Activity.this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initiate the request to get users usage information
     */
    private void getCreditInfo() {

        if (!(sharedPreferences.getString("mobile", "").equals(""))
                && !(sharedPreferences.getString("password", "")).equals("")) {

            working = true;
            displayLoadingView(true);
            try {
                accountProcessorFragment.execute();
            } catch (KeyStoreException e) {
                showNotification(e.getLocalizedMessage());
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                showNotification(e.getLocalizedMessage());
                e.printStackTrace();
            } catch (CertificateException e) {
                showNotification(e.getLocalizedMessage());
                e.printStackTrace();
            } catch (IOException e) {
                showNotification(e.getLocalizedMessage());
                e.printStackTrace();
            }

        } else {
            showRegisterDialog();
        }
    }

    /**
     * Alert the user that this app can communicate via an intermediate server.
     * It's possible they may not want this functionality active if they don't
     * trust my server.
     */
    private void showInfoDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SherlockDialogFragment dialog = new InfoDialog();
        dialog.show(ft, "dialog");
    }

    /**
     * User has no username or password entered.
     */
    private void showRegisterDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SherlockDialogFragment dialog = new RegisterDialog();
        dialog.show(ft, "dialog");
    }

    /**
     * Callback function to handle when usage has been retrieved and parsed
     */
    @Override
    public void onAccountUsageReceived() {

        List<BaseItem> usageItems = accountProcessorFragment.getItems();

        if (usageItems != null) {
            updateLastRefreshedTextView(accountProcessorFragment.getDate());
            displayUsages(usageItems);

        }
        displayLoadingView(false);

        working = false;
    }

    /**
     * Exception callback receiver
     *
     * @param exception Exception from fetching usages
     */
    @Override
    public void onAccountUsageExceptionReceived(String exception) {
        displayLoadingView(false);
        working = false;
        showNotification(exception);

        // TODO: should we display last persisted usages if they are not already shown ?
        // there was an error fetching usages, just show the last persisted usage info, as it
        // may not already be displayed, Usually this is the case if user auto refreshes on
        // app startup, or device config changed while fetching a usage.
        loadPersistedUsages();
    }

    /**
     * Show the error to the user
     *
     * @param exception Exception message
     */
    private void showNotification(String exception) {

        LayoutInflater inflater = getLayoutInflater();
        // Inflate the Layout
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) findViewById(R.id.toastLayout));

        TextView text = (TextView) layout.findViewById(R.id.toastTextView);
        // Set the Text to show in TextView
        text.setText("Error: " + exception);
        Toast toast = new Toast(getApplicationContext());
        toast.getView();
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    /**
     * just show a spinning {@link ProgressBar} while we are waiting for result.
     *
     * @param paramBoolean Show/Hide ProgressBar
     */
    private void displayLoadingView(boolean paramBoolean) {

        if (updatingView == null) {
            updatingView = new UpdatingView(getBaseContext());
            parentView.addView(updatingView, new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }

        if (parentView != null) {
            if (paramBoolean) {
                updatingView.setVisibility(View.VISIBLE);
                updatingView.bringToFront();
            } else {
                updatingView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Display the users usages.
     *
     * @param usageItems Usages Retrieved
     */
    private void displayUsages(List<BaseItem> usageItems) {

        if (scrollView == null) {
            scrollView = new ScrollView(this);

            // add the ScrollView to the top of the RelativeLayout, but do not overlap the lastRefreshed TextView
            RelativeLayout.LayoutParams scrollViewLayoutParams = new
                    RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            parentView.addView(scrollView, scrollViewLayoutParams);
            RelativeLayout.LayoutParams relativeLayoutParameters =
                    (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
            relativeLayoutParameters.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            relativeLayoutParameters.addRule(RelativeLayout.ABOVE, lastRefreshed.getId());
        }

        if (baseUsageView == null) {

            baseUsageView = new LinearLayout(this);
            baseUsageView.setOrientation(LinearLayout.VERTICAL);
            scrollView.addView(baseUsageView, new ScrollView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }

        if (usageItems != null) {

            baseUsageView.removeAllViews();
            baseUsageView.setVisibility(View.GONE);

            List<BaseItemsGroupedAndSorted> baseItemsGroupedAndSorted = new OrganiseItems(
                    usageItems).groupUsages();

            if (baseItemsGroupedAndSorted != null) {

                // just to test scroll view over text view
                for (BaseItemsGroupedAndSorted b : baseItemsGroupedAndSorted) {

                    UsageView l = new UsageView(getBaseContext(), b);
                    baseUsageView.addView(l);

                }

                baseUsageView.setVisibility(View.VISIBLE);
                refreshDoneSinceLoadingPersistedData = true;
            }
        }
    }
}