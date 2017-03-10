package com.phuocnguyen.filestransferftp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.phuocnguyen.filestransferftp.R;
import com.phuocnguyen.filestransferftp.adapters.AccountsArrayAdapter;
import com.phuocnguyen.filestransferftp.database.DataHelper;
import com.phuocnguyen.filestransferftp.interfaces.ResponseInterface;
import com.phuocnguyen.filestransferftp.models.FTPAccount;
import com.phuocnguyen.filestransferftp.models.ResponseData;
import com.phuocnguyen.filestransferftp.utils.FilesFTPUtils;
import com.phuocnguyen.filestransferftp.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phuocnguyen on 10 Mar 2017.
 */

public class AccountsActivity extends AppCompatActivity{

    public static final String EXTRA_IS_CONNECTED = "_EXTRA_IS_CONNECTED";

    private ListView mLvAccounts;
    private List<FTPAccount> mFTPAccountList;
    private AccountsArrayAdapter mAccountsArrayAdapter;
    private FloatingActionButton mBtnAdd;
    public static int mPositionEdit;
    private DataHelper mDb;


    public static final int ACTIVITY_ADDING_ACCOUNT = 101;
    public static final int ACTIVITY_EDITING_ACCOUNT = 102;
    private static final String FTPACCOUNT = "_ftp_account";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        mLvAccounts = (ListView) findViewById(R.id.act_lv_accounts);
        mBtnAdd = (FloatingActionButton) findViewById(R.id.act_accounts_btn_add);
        mDb = DataHelper.getInstance(AccountsActivity.this);
        if (savedInstanceState == null) {
            mFTPAccountList = mDb.getAllAccounts();
        }else {
            mFTPAccountList = savedInstanceState.getParcelableArrayList(FTPACCOUNT);
        }
        mAccountsArrayAdapter = new AccountsArrayAdapter(AccountsActivity.this, R.layout.item_account, mFTPAccountList);
        mLvAccounts.setAdapter(mAccountsArrayAdapter);
        mAccountsArrayAdapter.notifyDataSetChanged();

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountsActivity.this, AccountEditorActivity.class)
                        .putExtra(AccountEditorActivity.EXTRA_NEW, true);
                startActivityForResult(intent, ACTIVITY_ADDING_ACCOUNT);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });

        mLvAccounts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Utilities.showThreeQuestion(
                        AccountsActivity.this,
                        null,
                        getString(R.string.custom_dialog_question),
                        editOneFTPAccount(
                                mFTPAccountList.get(position)), deleteOneFTPAccount(mFTPAccountList.get(position)),
                        getString(R.string.empty), getString(R.string.empty));
                return true;
            }
        });

        mLvAccounts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connectFTPAccount(mFTPAccountList.get(position));
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(FTPACCOUNT, (ArrayList<? extends Parcelable>) mFTPAccountList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_OK) {
            if (requestCode == ACTIVITY_ADDING_ACCOUNT) {
                FTPAccount ftpAccount = new FTPAccount();
                ftpAccount.setmHost(data.getExtras().getString(
                        AccountEditorActivity.EXTRA_ACCOUNT_HOST));
                ftpAccount.setmUsername(data.getExtras().getString(
                        AccountEditorActivity.EXTRA_ACCOUNT_USERNAME));
                ftpAccount.setmPort(data.getExtras().getInt(
                        AccountEditorActivity.EXTRA_ACCOUNT_PORT));
                ftpAccount.setmPassword(data.getExtras().getString(
                        AccountEditorActivity.EXTRA_ACCOUNT_PASSWORD));
                mFTPAccountList.add(ftpAccount);
                mDb.insertAnAccount(ftpAccount);
                mAccountsArrayAdapter.notifyDataSetChanged();
            }else if (requestCode == ACTIVITY_EDITING_ACCOUNT) {
                FTPAccount ftpAccount = mFTPAccountList.get(mPositionEdit);
                ftpAccount.setmHost(data.getExtras().getString(
                        AccountEditorActivity.EXTRA_ACCOUNT_HOST));
                ftpAccount.setmUsername(data.getExtras().getString(
                        AccountEditorActivity.EXTRA_ACCOUNT_USERNAME));
                ftpAccount.setmPort(data.getExtras().getInt(
                        AccountEditorActivity.EXTRA_ACCOUNT_PORT));
                ftpAccount.setmPassword(data.getExtras().getString(
                        AccountEditorActivity.EXTRA_ACCOUNT_PASSWORD));
                mDb.updateOneAccount(ftpAccount);
                mAccountsArrayAdapter.notifyDataSetChanged();
            }
        }
    }

    private Runnable editOneFTPAccount(final FTPAccount ftpAccount) {
        Runnable r1 = new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(AccountsActivity.this,
                        AccountEditorActivity.class).putExtra(
                        AccountEditorActivity.EXTRA_ACCOUNT_HOST,
                        ftpAccount.getmHost());
                intent.putExtra(
                        AccountEditorActivity.EXTRA_ACCOUNT_PORT,
                        ftpAccount.getmPort());
                intent.putExtra(
                        AccountEditorActivity.EXTRA_ACCOUNT_USERNAME,
                        ftpAccount.getmUsername());
                intent.putExtra(
                        AccountEditorActivity.EXTRA_ACCOUNT_PASSWORD,
                        ftpAccount.getmPassword());
                startActivityForResult(intent,
                        ACTIVITY_EDITING_ACCOUNT);
            }
        };
        return r1;
    }

    private Runnable deleteOneFTPAccount(final FTPAccount ftpAccount){
        return new Runnable() {
            @Override
            public void run() {
                mDb.deleteAccount(ftpAccount.getmId());
                mFTPAccountList.remove(ftpAccount);
                mAccountsArrayAdapter.notifyDataSetChanged();
            }
        };
    }

    private void connectFTPAccount(FTPAccount ftpAccount){
        Utilities.showProgressDialog(AccountsActivity.this, getString(R.string.dialog_msg_waiting));
        FilesFTPUtils.getInstance().executeConnect(ftpAccount.getmHost(), ftpAccount.getmPort(),
                ftpAccount.getmUsername(), ftpAccount.getmPassword(), new ResponseInterface() {
                    @Override
                    public void processResponseData(ResponseData responseData) {
                        Utilities.hideProgressDialog();
                        if(responseData.getmStatusCode() == FilesFTPUtils.CONNECT_FTP_SERVER_SUCCESS){
                            Intent returnData = new Intent();
                            returnData.putExtra(EXTRA_IS_CONNECTED, true);
                            setResult(RESULT_OK, returnData);
                            onBackPressed();
                        }else {
                            Snackbar.make(mLvAccounts, responseData.getmData(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }
}
