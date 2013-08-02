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

package damo.three.ie.fragment;

import android.app.Activity;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragment;
import damo.three.ie.my3.AccountProcessor;
import damo.three.ie.my3usage.BaseItem;
import damo.three.ie.util.DateUtils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

public class AccountProcessorFragment extends SherlockFragment {

    private Boolean working = false;
    private List<BaseItem> items = null;
    private String dateNow = null;

    /**
     * Callback interface through which the fragment will report the task's
     * progress and results back to the Activity.
     */
    public interface AccountProcessorListener {
        void onAccountUsageReceived();

        void onAccountUsageExceptionReceived(String exception);

    }

    private AccountProcessor accountProcessor;
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

    }

    /**
     * Set the callback to null so we don't accidentally leak the Activity
     * instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();

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
     *
     * @throws KeyManagementException
     * @throws UnrecoverableKeyException
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
        accountProcessor = new AccountProcessor(this);
        accountProcessor.execute();

    }

    /**
     * Pass back exception to {@link damo.three.ie.activity.My3Activity}
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
     * Pass back exception to {@link damo.three.ie.activity.My3Activity}
     *
     * @param usages Usages retrieved
     */
    public void reportBackUsages(List<BaseItem> usages) {
        working = false;
        items = usages;
        if (accountProcessorListener != null) {
            dateNow = DateUtils.dateNowAsString();
            accountProcessorListener.onAccountUsageReceived();
        }

    }

    /**
     * Provide a method to the activity to know the ASync task is still working
     * if the Activity was re-created.
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
     * @return Date the usages were fetched
     */
    public String getDate() {
        return dateNow;
    }

}
