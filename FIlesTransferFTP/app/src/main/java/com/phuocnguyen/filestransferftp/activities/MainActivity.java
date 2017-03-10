package com.phuocnguyen.filestransferftp.activities;

import android.content.Intent;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.phuocnguyen.filestransferftp.R;
import com.phuocnguyen.filestransferftp.adapters.FilesPagerAdapter;
import com.phuocnguyen.filestransferftp.fragments.LocalFilesFragment;
import com.phuocnguyen.filestransferftp.fragments.RemoteFilesFragment;
import com.phuocnguyen.filestransferftp.utils.FilesFTPUtils;
import com.phuocnguyen.filestransferftp.utils.LoggerUtil;


public class MainActivity extends AppCompatActivity {

    public static final int ACTIVITY_ACCOUNTS = 103;

    private ViewPager mPager;
    private TabLayout mTabLayout;
    private FilesPagerAdapter mFilesPagerAdapter;
    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.local_title));
        mTabLayout = (TabLayout) findViewById(R.id.act_main_tab);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.local_title));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.remote_title));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        mPager = (ViewPager) findViewById(R.id.act_main_pager);
        mFilesPagerAdapter = new FilesPagerAdapter(this.getSupportFragmentManager(), MainActivity.this);
        mPager.setAdapter(mFilesPagerAdapter);
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
                getSupportActionBar().setTitle(tab.getText());
                updateUI(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public ActionMode getmActionMode() {
        return mActionMode;
    }

    private void updateUI(int pos){
        switch (pos){
            case 0:

                break;
            case 1:
                    RemoteFilesFragment remoteFilesFragment = (RemoteFilesFragment) mFilesPagerAdapter.getItem(1);
                    LoggerUtil.d("MainActivity", "updateUI()", remoteFilesFragment.getmListFiles() + "");
                    if(remoteFilesFragment.getmListFiles() == null || remoteFilesFragment.getmListFiles().size() == 0)
                        remoteFilesFragment.loadFiles();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(mPager.getCurrentItem() == 0){
            menu.getItem(1).setVisible(false);
        }else {
            menu.getItem(1).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_refresh:
                updateUI(mPager.getCurrentItem());
                break;
            case R.id.menu_accounts:
                Intent intent = new Intent(MainActivity.this, AccountsActivity.class);
                startActivityForResult(intent, ACTIVITY_ACCOUNTS);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_OK && requestCode == ACTIVITY_ACCOUNTS) {
            updateUI(mPager.getCurrentItem());
        }
    }

    @Override
    public void onBackPressed() {
        if(mPager.getCurrentItem() == 0){
            LocalFilesFragment localFilesFragment = (LocalFilesFragment) mFilesPagerAdapter.getItem(mPager.getCurrentItem());
            localFilesFragment.doBackPress();
        }else
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        FilesFTPUtils.getInstance().logoutFTPServer();
        super.onDestroy();
    }
}
