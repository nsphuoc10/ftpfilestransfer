package com.phuocnguyen.filestransferftp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.phuocnguyen.filestransferftp.R;
import com.phuocnguyen.filestransferftp.utils.LoggerUtil;
import com.phuocnguyen.filestransferftp.utils.Utilities;

/**
 * Created by phuocnguyen on 10 Mar 2017.
 */

public class AccountEditorActivity extends AppCompatActivity{

    public static final String EXTRA_ACCOUNT_HOST = "_EXTRA_ACCOUNT_HOST";
    public static final String EXTRA_ACCOUNT_PORT = "_EXTRA_ACCOUNT_PORT";
    public static final String EXTRA_ACCOUNT_USERNAME = "_EXTRA_ACCOUNT_USERNAME";
    public static final String EXTRA_ACCOUNT_PASSWORD = "_EXTRA_ACCOUNT_PASSWORD";
    public static final String EXTRA_NEW = "_EXTRA_NEW";

    private EditText mEditHost;
    private EditText mEditPort;
    private EditText mEditUsername;
    private EditText mEditPassword;
    private CoordinatorLayout mCoorLayoutWrapper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_editor);

        mCoorLayoutWrapper = (CoordinatorLayout) findViewById(R.id.act_acc_editor_coor_wrapper);
        mEditHost = (EditText) findViewById(R.id.act_acc_editor_edit_host);
        mEditPort = (EditText) findViewById(R.id.act_acc_editor_edit_port);
        mEditUsername = (EditText) findViewById(R.id.act_acc_editor_edit_username);
        mEditPassword = (EditText) findViewById(R.id.act_acc_editor_edit_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        boolean isCreateNew = getIntent().getBooleanExtra(EXTRA_NEW, false);
        if (!isCreateNew) {
            mEditHost.setText(getIntent().getStringExtra(EXTRA_ACCOUNT_HOST));
            String port = getIntent().getIntExtra(EXTRA_ACCOUNT_PORT, 21) + "";
            mEditPort.setText(port);
            mEditUsername.setText(getIntent().getStringExtra(EXTRA_ACCOUNT_USERNAME));
            mEditPassword.setText(getIntent().getStringExtra(EXTRA_ACCOUNT_PASSWORD));
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Utilities.hideSystemUI(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_checked:
                if (validateFields()) {
                    Intent returnData = new Intent();
                    returnData.putExtra(EXTRA_ACCOUNT_HOST, mEditHost.getText()
                            .toString());
                    returnData.putExtra(EXTRA_ACCOUNT_PORT, Integer.parseInt(mEditPort
                            .getText().toString()));
                    returnData.putExtra(EXTRA_ACCOUNT_USERNAME, mEditUsername
                            .getText().toString());
                    returnData.putExtra(EXTRA_ACCOUNT_PASSWORD, mEditPassword
                            .getText().toString());
                    LoggerUtil.d("AccountEditorActivity", "onOptionsItemSelected()");
                    setResult(RESULT_OK, returnData);
                    onBackPressed();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateFields(){
        if (mEditHost.getText().toString().equals("")) {
            Snackbar.make(mCoorLayoutWrapper, getString(R.string.act_acc_editor_host_empty), Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (mEditPort.getText().toString().equals("")) {
            Snackbar.make(mCoorLayoutWrapper, getString(R.string.act_acc_editor_port_empty), Snackbar.LENGTH_SHORT).show();
            return false;
        }else if (mEditUsername.getText().toString().equals("")) {
            Snackbar.make(mCoorLayoutWrapper, getString(R.string.act_acc_editor_username_empty), Snackbar.LENGTH_SHORT).show();
            return false;
        }else if (mEditPassword.getText().toString().equals("")) {
            Snackbar.make(mCoorLayoutWrapper, getString(R.string.act_acc_editor_host_empty), Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }
}
