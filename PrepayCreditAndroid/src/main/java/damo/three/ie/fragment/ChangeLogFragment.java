/*
 * This file is part of Prepay Credit for Android
 *
 * Copyright © 2013  Damien O'Reilly
 *
 * Prepay Credit for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import damo.three.ie.R;
import damo.three.ie.util.CustomTagHandler;
import damo.three.ie.util.FileUtils;

import java.io.IOException;

public class ChangeLogFragment extends Fragment {

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_changelog, container, false);
        String content = "";
        try {
            content = FileUtils.readFile(getActivity(), R.raw.changelog);
        } catch (IOException e) {
            /* something went wrong reading the raw file */
            e.printStackTrace();
        }

        TextView aboutTextView = (TextView) view.findViewById(R.id.changelog_text);
        aboutTextView.setMovementMethod(LinkMovementMethod.getInstance());
        aboutTextView.setText(Html.fromHtml(content, null, new CustomTagHandler()));

        return view;
    }
}