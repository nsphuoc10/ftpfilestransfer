package com.phuocnguyen.filestransferftp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.phuocnguyen.filestransferftp.R;
import com.phuocnguyen.filestransferftp.activities.MainActivity;
import com.phuocnguyen.filestransferftp.adapters.LocalFilesArrayAdapter;
import com.phuocnguyen.filestransferftp.interfaces.MenuActionBarCallBack;
import com.phuocnguyen.filestransferftp.interfaces.ResponseInterface;
import com.phuocnguyen.filestransferftp.models.ResponseData;
import com.phuocnguyen.filestransferftp.utils.FilesFTPUtils;
import com.phuocnguyen.filestransferftp.utils.LoggerUtil;
import com.phuocnguyen.filestransferftp.utils.Utilities;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by phuocnguyen on 8 Mar 2017.
 */

public class LocalFilesFragment extends Fragment{

    private String mTitle;
    private MainActivity mActivity;
    private LocalFilesArrayAdapter mLocalFilesArrayAdapter;
    private List<File> mListFiles;
    private List<File> mListUploadFiles;
    private LinearLayout mLinFolders;
    private ListView mLvFiles;
    private HorizontalScrollView mHScrV;
    private int mFolderPos = -1;

    public static LocalFilesFragment newInstance(String mTitle){
        LocalFilesFragment localFilesFragment = new LocalFilesFragment();
        localFilesFragment.setmTitle(mTitle);
        return localFilesFragment;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (MainActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListFiles = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_files, null);
        mLinFolders = (LinearLayout) root.findViewById(R.id.frag_files_lin_folder);
        mHScrV = (HorizontalScrollView) root.findViewById(R.id.frag_files_h_scrv);
        mLvFiles = (ListView) root.findViewById(R.id.frag_files_lv);
        mListFiles = new ArrayList<>();
        mListUploadFiles = new ArrayList<>();
        mLocalFilesArrayAdapter = new LocalFilesArrayAdapter(mActivity, R.layout.item_file, mListFiles);
        mLvFiles.setAdapter(mLocalFilesArrayAdapter);
        mLocalFilesArrayAdapter.notifyDataSetChanged();
        loadLocalFiles();
        mLvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LoggerUtil.d("LocalFilesFragment", "onItemClick()", "Lalala");
                if(!mListFiles.get(position).isDirectory()){
                    Utilities.handleOpenAFile(mActivity, mListFiles.get(position), mLvFiles);
                }else {
                    LoggerUtil.d("LocalFilesFragment", "onItemClick()", "Here's a directory. " + mListFiles.get(position).getAbsolutePath());
                    addNewItemFolder(mListFiles.get(position).getName(), mListFiles.get(position).getAbsolutePath());
                    updateLvFolders(mListFiles.get(position).getAbsolutePath());
                }
            }
        });
        mLvFiles.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(!mListFiles.get(position).isDirectory()){
                    /*mActivity.startActionMode(new MenuActionBarCallBack(R.id.menu_upload));
                    mListUploadFiles.add(mListFiles.get(position));*/
                    File file = mListFiles.get(position);
                    Utilities.showQuestion(mActivity, getString(R.string.dialog_msg_upload),
                            uploadFiles(file.getName(), file.getAbsolutePath()));
                }
                return true;
            }
        });
        return root;
    }

    private Runnable uploadFiles(final String fileName, final String localFilePath){
        return new Runnable() {
            @Override
            public void run() {
                if(Utilities.checkFTPServerIsConnected(mActivity, mLvFiles)){
                    Utilities.showProgressDialog(mActivity, getString(R.string.dialog_msg_waiting));
                    FilesFTPUtils.getInstance().executeUpload(FilesFTPUtils.DIR_REMOTE,
                            new String[]{fileName}, new String[]{localFilePath}, new ResponseInterface() {
                                @Override
                                public void processResponseData(ResponseData responseData) {
                                    Utilities.hideProgressDialog();
                                    LoggerUtil.d("LocalFilesFragment", "processResponseData()", responseData.getmData());
                                    if(responseData.getmStatusCode() == FilesFTPUtils.TRANSFER_FILE_SUCCESS){
                                        Snackbar.make(mLvFiles, responseData.getmData(), Snackbar.LENGTH_SHORT).show();
                                    }else {
                                        Snackbar.make(mLvFiles, responseData.getmData(), Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        };
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void addNewItemFolder(String title, String path){
        TextView tv = new TextView(mActivity);
        LoggerUtil.d("LocalFilesFragment", "addNewItemFolder()", path);
        tv.setText(String.format(Locale.getDefault(), "%s %s", title, getString(R.string.frag_files_greater)));
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        tv.setTag(path);
        tv.setPadding(10, 10, 10, 10);
        tv.setClickable(true);
        final int tvPos = mLinFolders.getChildCount();
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoggerUtil.d("LocalFilesFragment", "onClick()");
                mFolderPos = tvPos;
                updateLinFolders();
            }
        });
        mLinFolders.addView(tv);
        mHScrV.smoothScrollTo(mHScrV.getWidth(), 0);
    }

    private void updateLinFolders(){
        LoggerUtil.d("LocalFilesFragment", "updateLinFolders()", mFolderPos + "");
        if(mFolderPos < mLinFolders.getChildCount()){
            if(mFolderPos + 1 < mLinFolders.getChildCount()){
                LoggerUtil.d("LocalFilesFragment", "updateLinFolders()", mFolderPos + ";" + mLinFolders.getChildCount() + "; " + (mLinFolders.getChildCount()-mFolderPos-1));
                mLinFolders.removeViewsInLayout(mFolderPos+1, mLinFolders.getChildCount()-mFolderPos-1);
            }
            TextView tvSelectedFolder = (TextView) mLinFolders.getChildAt(mFolderPos);
            if(tvSelectedFolder != null){
                updateLvFolders(tvSelectedFolder.getTag().toString());
            }
            LoggerUtil.d("LocalFilesFragment", "updateLinFolders()", "return parent dir" + tvSelectedFolder.getTag());
        }

    }

    private void updateLvFolders(String path){
        File file = new File(path);
        mListFiles.clear();
        mListFiles.addAll(Arrays.asList(file.listFiles()));
        LoggerUtil.d("LocalFilesFragment", "updateLvFolders()", file.getAbsolutePath() + ";" + file.listFiles().length);
        mLocalFilesArrayAdapter.notifyDataSetChanged();
    }

    private void loadLocalFiles(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if(Utilities.checkSDCardExternalExisted()) {
                    String[] temp = Environment.getRootDirectory().list();
                    String tt = "";
                    if(temp != null)
                        for(String str : temp){
                            tt += str + ";";
                        }
                    LoggerUtil.d("LocalFilesFragment", "onCreateView()", tt + "");
                    File sdFile = Environment.getExternalStorageDirectory();
                    mListFiles.addAll(Arrays.asList(sdFile.listFiles()));
                    addNewItemFolder(Environment.getExternalStorageDirectory().getName(), sdFile.getAbsolutePath());
                }
            }
        });
    }

    public void doBackPress(){
        if(mListUploadFiles.size() > 0){
            mListUploadFiles.clear();
            mActivity.getmActionMode().finish();
        }

        try {
            if(mLinFolders.getChildCount() == 1){
                mActivity.finish();
            }else {
                mFolderPos = mLinFolders.getChildCount()-2;
                updateLinFolders();
            }
        }catch (Exception e){
            LoggerUtil.d("LocalFilesFragment", "doBackPress()");
        }
    }
}
