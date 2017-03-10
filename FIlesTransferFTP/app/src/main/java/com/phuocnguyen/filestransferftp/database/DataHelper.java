package com.phuocnguyen.filestransferftp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.phuocnguyen.filestransferftp.models.FTPAccount;
import com.phuocnguyen.filestransferftp.utils.LoggerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phuocnguyen on 18 Feb 2016.
 * Modified on 10 Mar 2017
 */
public class DataHelper {

    /*---------------------------------Thông tin của user--------------------------------------*/
    private static final String ID = "_id";
    private static final String Host = "_host";
    private static final String Port = "_port";
    private static final String Username = "_username";
    private static final String Password = "_password";



    /**
     * Khởi tạo CSDL
     */
    private DatabaseHelper mDbhepler;
    private SQLiteDatabase mDb;
    private Context mContext;
    private static final int DATABASE_VERSION = 1;

    private static final String ACCOUNT_TABLE = "_ACCOUNT";
    private static final String DB_NAME = "_FTP_ACCOUNT";

    private static final String CREATE_CHILD_TABLE = "create table if not exists "
            + ACCOUNT_TABLE
            + "("
            + ID
            + " integer primary key not null, "
            + Host
            + " text, "
            + Port
            + " integer, "
            + Username
            + " text, "
            + Password
            + " text" + ")";


    private static DataHelper instance;

    public static DataHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DataHelper(context);
            instance.open();
        }

        return instance;
    }

    private DataHelper(Context ct) {
        this.mContext = ct;
    }

    public class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_CHILD_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE);
            onCreate(db);
        }

    }

    private DataHelper open() {
        mDbhepler = new DatabaseHelper(mContext, DB_NAME, null,
                DATABASE_VERSION);
        mDb = mDbhepler.getWritableDatabase();
        return this;
    }

    public long insertAnAccount(FTPAccount ftpAccount) {
        ContentValues value = new ContentValues();
        value.put(Host, ftpAccount.getmHost());
        value.put(Port, ftpAccount.getmPort());
        value.put(Username, ftpAccount.getmUsername());
        value.put(Password, ftpAccount.getmPassword());
        return mDb.insert(ACCOUNT_TABLE, null, value);
    }


    public void deleteAccount(int uId) {
        mDb.delete(ACCOUNT_TABLE, ID + "=?",
                new String[]{uId + ""});
    }

    public List<FTPAccount> getAllAccounts() {
        List<FTPAccount> list = new ArrayList<>();
        Cursor cursor = mDb.query(ACCOUNT_TABLE, new String[]{ID,
                Host, Port, Username, Password}, null, null, null, null, Username + " ASC");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    FTPAccount ftpAccount = new FTPAccount();
                    ftpAccount.setmId(cursor.getInt(0));
                    ftpAccount.setmHost(cursor.getString(1));
                    ftpAccount.setmPort(cursor.getInt(2));
                    ftpAccount.setmUsername(cursor.getString(3));
                    ftpAccount.setmPassword(cursor.getString(4));
                    list.add(ftpAccount);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return list;
    }

    public FTPAccount getOneAccount(String username) {
        FTPAccount ftpAccount = null;
        Cursor cursor = mDb.query(ACCOUNT_TABLE, new String[]{ID,
                        Host, Port, Username, Password}, Username + "=?",
                new String[]{username}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ftpAccount = new FTPAccount();
                    ftpAccount.setmId(cursor.getInt(0));
                    ftpAccount.setmHost(cursor.getString(1));
                    ftpAccount.setmPort(cursor.getInt(2));
                    ftpAccount.setmUsername(cursor.getString(3));
                    ftpAccount.setmPassword(cursor.getString(4));
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        return ftpAccount;
    }

    public void updateOneAccount(FTPAccount ftpAccount) {
        ContentValues values = new ContentValues();
        values.put(Host, ftpAccount.getmHost());
        values.put(Port, ftpAccount.getmPort());
        values.put(Username, ftpAccount.getmUsername());
        values.put(Password, ftpAccount.getmPassword());
        LoggerUtil.d("DataHelper", "updateOneAccount()", ftpAccount + "");
        mDb.update(ACCOUNT_TABLE, values, ID + "=?",
                new String[]{ftpAccount.getmId() + ""});
    }


}
