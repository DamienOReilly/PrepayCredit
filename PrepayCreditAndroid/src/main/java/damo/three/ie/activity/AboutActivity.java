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

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import damo.three.ie.R;
import damo.three.ie.ui.ViewPagerAdapter;

public class AboutActivity extends ActionBarActivity {

    private ActionBar actionBar;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        // Activate Navigation Mode Tabs
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.pager);

        // Activate Fragment Manager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Capture ViewPager page swipes
        ViewPager.SimpleOnPageChangeListener viewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                actionBar.setSelectedNavigationItem(position);
            }
        };

        viewPager.setOnPageChangeListener(viewPagerListener);
        // Locate the adapter class called ViewPagerAdapter.java
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragmentManager);
        // Set the View Pager Adapter into ViewPager
        viewPager.setAdapter(viewPagerAdapter);

        // Capture tab button clicks
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                // Pass the position on tab click to ViewPager
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            }

            @Override
            public void onTabReselected(Tab tab, FragmentTransaction ft) {
            }
        };

        // Create first Tab
        Tab tab = actionBar.newTab().setText(R.string.about).setTabListener(tabListener);
        actionBar.addTab(tab);

        // Create second Tab
        tab = actionBar.newTab().setText(R.string.changelog).setTabListener(tabListener);
        actionBar.addTab(tab);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // User pressed the return to home icon on the left on the ActionBar.
            case android.R.id.home:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}