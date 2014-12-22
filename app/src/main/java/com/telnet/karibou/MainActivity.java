package com.telnet.karibou;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.telnet.adapters.TabPagerAdapter;
import com.telnet.requests.PresenceTask;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity {
    ViewPager Tab;
    TabPagerAdapter TabAdapter;
    ActionBar actionBar;
    private Handler handler;
    private Timer timer;
    private TimerTask doAsynchronousTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabAdapter = new TabPagerAdapter(getSupportFragmentManager());

        Tab = (ViewPager) findViewById(R.id.pager);

        // Considere all three tabs visible (do not call onPause on them)
        Tab.setOffscreenPageLimit(3);
        Tab.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar = getActionBar();
                        actionBar.setSelectedNavigationItem(position);
                    }
                });
        Tab.setAdapter(TabAdapter);

        actionBar = getActionBar();
        //Enable Tabs on Action Bar
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabReselected(android.app.ActionBar.Tab tab,
                                        FragmentTransaction ft) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

                Tab.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(android.app.ActionBar.Tab tab,
                                        FragmentTransaction ft) {
                // TODO Auto-generated method stub

            }
        };
        //Add New Tab
        actionBar.addTab(actionBar.newTab().setText("En ligne").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Mini chat").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Flashmails").setTabListener(tabListener));
        actionBar.setSelectedNavigationItem(1);

        // Timer for Presence will be set in onResume
        handler = new Handler();
    }

    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    //    // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.login, menu);
    //    return true;
    //}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        // Execute task for Presence and change timer interval back to PRESENCE_REFRESH
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        doAsynchronousTask = new PresenceTimerTask();
        timer.schedule(doAsynchronousTask, 0, Constants.PRESENCE_REFRESH * 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Cancel timer
        timer.cancel();
    }

    private class PresenceTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.post(new Runnable() {
                public void run() {
                    try {
                        PresenceTask presenceTask = new PresenceTask();
                        presenceTask.execute(Constants.PRESENCE_URL);
                    } catch (Exception e) {
                    }
                }
            });
        }
    }
}
