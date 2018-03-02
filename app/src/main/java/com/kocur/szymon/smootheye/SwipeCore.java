package com.kocur.szymon.smootheye;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by Szymon Kocur on 2017-10-20.
 */

public class SwipeCore extends AppCompatActivity implements ActionBar.TabListener {
    private ViewPager viewPager;
    private SwipeAdapter mAdapter;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        int fragmentID = getIntent().getIntExtra("fragmentID", 1);


        // Initialization
        viewPager = (ViewPager) findViewById(R.id.pager);
        //actionBar = getActionBar();
        mAdapter = new SwipeAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(fragmentID, false);

        /*actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.icon_more).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.logo_smootheye).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.icon_settings).setTabListener(this));

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        }); */
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }
}
