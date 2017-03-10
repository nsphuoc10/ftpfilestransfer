package com.phuocnguyen.filestransferftp.interfaces;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.phuocnguyen.filestransferftp.R;

/**
 * Created by phuocnguyen on 11 Mar 2017.
 */

public class MenuActionBarCallBack implements ActionMode.Callback{
    private int mTypeMenu;
    private Runnable mRunClickItem;
    private Runnable mRunDestroy;

    public MenuActionBarCallBack(int mTypeMenu, Runnable mRunClickItem, Runnable mRunDestroy) {
        this.mTypeMenu = mTypeMenu;
        this.mRunClickItem = mRunClickItem;
        this.mRunDestroy = mRunDestroy;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        switch (mTypeMenu){
            case R.id.menu_download:
                mode.getMenuInflater().inflate(R.menu.menu_download, menu);
                break;
            default:
                mode.getMenuInflater().inflate(R.menu.menu_upload, menu);
                break;
        }
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        mode.setTitle("Select files");
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_download:
                break;
            case R.id.menu_upload:
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }
}
