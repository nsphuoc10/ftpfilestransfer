package com.phuocnguyen.filestransferftp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.phuocnguyen.filestransferftp.R;
import com.phuocnguyen.filestransferftp.adapters.RemoteFilesArrayAdapter;
import com.phuocnguyen.filestransferftp.interfaces.ResponseInterface;
import com.phuocnguyen.filestransferftp.models.ResponseData;
import com.phuocnguyen.filestransferftp.utils.FilesFTPUtils;
import com.phuocnguyen.filestransferftp.utils.LoggerUtil;
import com.phuocnguyen.filestransferftp.utils.Utilities;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by phuocnguyen on 8 Mar 2017.
 */

public class RemoteFilesFragment extends Fragment {

    private String mTitle;
    private AppCompatActivity mActivity;
    private RemoteFilesArrayAdapter mRemoteFilesArrayAdapter;
    private List<FTPFile> mListFiles;
    private List<FTPFile> mListDownloadFiles;
    private LinearLayout mLinFolders;
    private ListView mLvFiles;
    private HorizontalScrollView mHScrV;
    private int mFolderPos = -1;
    private String mCurrentPath="";

    public static RemoteFilesFragment newInstance(String mTitle) {
        RemoteFilesFragment fragmentLocalFiles = new RemoteFilesFragment();
        fragmentLocalFiles.setmTitle(mTitle);
        return fragmentLocalFiles;
    }

    public void loadFiles() {
        LoggerUtil.d("RemoteFilesFragment", "loadFiles()", "start load");
        if(Utilities.checkFTPServerIsConnected(mActivity, mLvFiles)){
            if(mActivity != null)
                Utilities.showProgressDialog(mActivity, getString(R.string.dialog_msg_waiting));
            FilesFTPUtils.getInstance().executeGetFiles("", new ResponseInterface() {
                @Override
                public void processResponseData(ResponseData responseData) {
                    LoggerUtil.d("RemoteFilesFragment", "loadFiles()", responseData.toString());
                    Utilities.hideProgressDialog();
                    if(responseData.getmStatusCode() == FilesFTPUtils.TRANSFER_FILE_SUCCESS){
                        List<FTPFile> ftpFileList = responseData.getmFileTransferList();
                        if(ftpFileList.size() > 0){
                            if(mListFiles == null)
                                mListFiles = new ArrayList<>();
                            mListFiles.clear();
                            mListFiles.addAll(ftpFileList);
                            if(mRemoteFilesArrayAdapter == null){
                                mRemoteFilesArrayAdapter = new RemoteFilesArrayAdapter(mActivity, R.layout.item_file, mListFiles);
                            }
                            mRemoteFilesArrayAdapter.notifyDataSetChanged();
                        }
                    }else {
                        Snackbar.make(mLvFiles, responseData.getmData(), Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (AppCompatActivity) context;
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
        addNewItemFolder("root", "");
        mRemoteFilesArrayAdapter = new RemoteFilesArrayAdapter(mActivity, R.layout.item_file, mListFiles);
        mLvFiles.setAdapter(mRemoteFilesArrayAdapter);
        mRemoteFilesArrayAdapter.notifyDataSetChanged();
        loadFiles();
        mLvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LoggerUtil.d("RemoteFilesFragment", "onItemClick()", mListFiles.get(position).getName());
                if(mListFiles.get(position).isDirectory()){
                    if(!mCurrentPath.equalsIgnoreCase(""))
                        mCurrentPath += "/";
                    mCurrentPath += mListFiles.get(position).getName();
                    updateLvFolders(mCurrentPath);
                    addNewItemFolder(mListFiles.get(position).getName(), mCurrentPath);
                }else {
                    String path;
                    if(!mCurrentPath.equalsIgnoreCase(""))
                        path = mCurrentPath + "/" + mListFiles.get(position).getName();
                    else
                        path = mListFiles.get(position).getName();
                    Utilities.showQuestion(mActivity, getString(R.string.dialog_msg_download),
                            startDownload(mListFiles.get(position).getName(), path));
                }
            }
        });
        return root;
    }

    private Runnable startDownload(final String fileName, final String filePathRemote){
        return new Runnable() {
            @Override
            public void run() {
                LoggerUtil.d("RemoteFilesFragment", "startDownload()", "start download");
                if(Utilities.checkFTPServerIsConnected(mActivity, mLvFiles)){
                    Utilities.showProgressDialog(mActivity, getString(R.string.dialog_msg_waiting));
                    FilesFTPUtils.getInstance().executeDownload(FilesFTPUtils.DIR_TRANSFER,
                            new String[]{filePathRemote}, new String[]{fileName}, new ResponseInterface() {
                                @Override
                                public void processResponseData(ResponseData responseData) {
                                    LoggerUtil.d("RemoteFilesFragment", "processResponseData()", responseData.getmData());
                                    Utilities.hideProgressDialog();
                                    if(responseData.getmStatusCode() == FilesFTPUtils.TRANSFER_FILE_SUCCESS){
                                        File file = Utilities.getOutputFile(FilesFTPUtils.DIR_TRANSFER, fileName);
                                        Utilities.handleOpenAFile(mActivity, file, mLvFiles);
                                    }else {
                                        Snackbar.make(mLvFiles, responseData.getmData(), Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        };
    }


    public List<FTPFile> getmListFiles() {
        return mListFiles;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void addNewItemFolder(String title, String path) {
        TextView tv = new TextView(mActivity);
        LoggerUtil.d("RemoteFilesFragment", "addNewItemFolder()", path);
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
                LoggerUtil.d("RemoteFilesFragment", "onClick()");
                mFolderPos = tvPos;
                updateLinFolders();
            }
        });
        mLinFolders.addView(tv);
        mHScrV.smoothScrollTo(mHScrV.getWidth(), 0);
    }

    private void updateLinFolders() {
        LoggerUtil.d("RemoteFilesFragment", "updateLinFolders()", mFolderPos + "");
        if (mFolderPos < mLinFolders.getChildCount()) {
            if (mFolderPos + 1 < mLinFolders.getChildCount()) {
                LoggerUtil.d("RemoteFilesFragment", "updateLinFolders()", mFolderPos + ";" + mLinFolders.getChildCount() + "; " + (mLinFolders.getChildCount() - mFolderPos - 1));
                mLinFolders.removeViewsInLayout(mFolderPos + 1, mLinFolders.getChildCount() - mFolderPos - 1);
            }
            TextView tvSelectedFolder = (TextView) mLinFolders.getChildAt(mFolderPos);
            if (tvSelectedFolder != null) {
                updateLvFolders(tvSelectedFolder.getTag().toString());
                mCurrentPath = tvSelectedFolder.getTag().toString();
            }
            LoggerUtil.d("RemoteFilesFragment", "updateLinFolders()", "return parent dir" + tvSelectedFolder.getTag());
        }

    }

    private void updateLvFolders(String path) {
        if(Utilities.checkFTPServerIsConnected(mActivity, mLvFiles)){
            Utilities.showProgressDialog(mActivity, getString(R.string.dialog_msg_waiting));
            FilesFTPUtils.getInstance().executeGetFiles(path, new ResponseInterface() {
                @Override
                public void processResponseData(ResponseData responseData) {
                    Utilities.hideProgressDialog();
                    if(responseData.getmStatusCode() == FilesFTPUtils.TRANSFER_FILE_SUCCESS){
                        if(responseData.getmFileTransferList() != null && responseData.getmFileTransferList().size() > 0){
                            mListFiles.clear();
                            mListFiles.addAll(responseData.getmFileTransferList());
                            mRemoteFilesArrayAdapter.notifyDataSetChanged();
                        }
                    }else
                        Snackbar.make(mLvFiles, responseData.getmData(), Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void doBackPress() {
        if (mLinFolders.getChildCount() == 1) {
            mActivity.finish();
        } else {
            mFolderPos = mLinFolders.getChildCount() - 2;
            updateLinFolders();
        }
    }
}
