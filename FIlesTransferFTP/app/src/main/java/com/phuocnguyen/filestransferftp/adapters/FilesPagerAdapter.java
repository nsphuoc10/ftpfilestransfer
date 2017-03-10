package com.phuocnguyen.filestransferftp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.phuocnguyen.filestransferftp.R;
import com.phuocnguyen.filestransferftp.activities.MainActivity;
import com.phuocnguyen.filestransferftp.fragments.LocalFilesFragment;
import com.phuocnguyen.filestransferftp.fragments.RemoteFilesFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phuocnguyen on 9 Mar 2017.
 */

public class FilesPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;
    private MainActivity mActivity;

    public FilesPagerAdapter(FragmentManager fm, MainActivity activity) {
        super(fm);
        mFragmentList = new ArrayList<>();
        mActivity = activity;
        mFragmentList.add(LocalFilesFragment.newInstance(mActivity.getString(R.string.local_title)));
        mFragmentList.add(RemoteFilesFragment.newInstance(mActivity.getString(R.string.remote_title)));
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
