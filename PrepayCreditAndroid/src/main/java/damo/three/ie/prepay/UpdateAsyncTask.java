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

import android.content.Context;
import android.os.AsyncTask;
import damo.three.ie.fragment.UpdateFragment;
import org.json.JSONArray;

/**
 * This class is responsible for logging into the My3 account and fetching details. Parsing data and returning usages
 * in JSON format.
 */
public class UpdateAsyncTask extends AsyncTask<Void, Void, JSONArray> {

    private final UpdateFragment updateFragment;
    private Context context;
    private Exception exception = null;
    private JSONArray jsonArray = null;


    /**
     * @param updateFragment Fragment that initialized this {@link AsyncTask}
     */
    public UpdateAsyncTask(UpdateFragment updateFragment) {
        this.context = updateFragment.getActivity().getApplicationContext();
        this.updateFragment = updateFragment;
    }

    /**
     * Call back to the fragment with usages
     *
     * @param jsonArray Usages in JSON
     */
    @Override
    protected void onPostExecute(JSONArray jsonArray) {

        if (exception != null) {
            updateFragment.reportBackException(exception);
        } else {
            updateFragment.reportBackUsages(jsonArray);
        }
    }

    /**
     * {@link AsyncTask} worker
     */
    @Override
    protected JSONArray doInBackground(Void... arg0) {
        try {
            UsageFetcher usageFetcher = new UsageFetcher(context, false);
            jsonArray = usageFetcher.getUsages();
            //return JSONUtils.jsonStringArraytoJsonArray(FileUtils.readFile(context, R.raw.test));
        } catch (Exception e) {
            exception = e;
        }
        // According to:
        // http://httpcomponents.10934.n7.nabble.com/how-do-I-close-connections-on-HttpClient-4-x-td13679.html
        // Apache Http library itself releases connections as needed. Also our HttpClient is a singleton, so we want it
        // to be reused. Therefore I'm not cleaning up in finally{} block.
        return jsonArray;
    }
}