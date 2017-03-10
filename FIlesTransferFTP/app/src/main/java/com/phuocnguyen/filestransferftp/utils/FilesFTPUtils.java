package com.phuocnguyen.filestransferftp.utils;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;

import com.phuocnguyen.filestransferftp.interfaces.ResponseInterface;
import com.phuocnguyen.filestransferftp.models.ResponseData;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by phuocnguyen on 3/8/2017.
 */

public class FilesFTPUtils {
    public static final String DIR_TRANSFER = "transferftp";
    public static final String DIR_REMOTE = "files";
    private static final String TAG = "FilesFTPUtils";
    private static final String REQUEST_DOWNLOAD = "request_download";
    private static final String REQUEST_UPLOAD = "request_upload";
    private static final String REQUEST_GET_FILES = "request_list";
    private static final String REQUEST_CONNECT = "request_connect";
    public static final int TRANSFER_FILE_FAILED = -1;
    public static final int TRANSFER_FILE_SUCCESS = 1;
    public static final int CONNECT_FTP_SERVER_SUCCESS = 10;
    public static final int CONNECT_FTP_SERVER_FAILED = 11;


    private FTPClient mFTPClient;

    private static FilesFTPUtils uniqueInstance;

    public static synchronized FilesFTPUtils getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new FilesFTPUtils();
        }
        return uniqueInstance;
    }

    public boolean isFTPConnected(){
        return mFTPClient != null && mFTPClient.isConnected();
    }

    private ResponseData connectFTPServer(String host, int port, String username, String password){

        ResponseData mResponseData = new ResponseData();
        if(mFTPClient == null)
            mFTPClient = new FTPClient();
        try {
            mFTPClient.connect(host, port);
            LoggerUtil.d("FilesFTPUtils", "connectFTPServer()", mFTPClient.getReplyString());
            mFTPClient.login(username, password);
            mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
            mFTPClient.enterLocalPassiveMode();
            mFTPClient.setKeepAlive(true);
            mFTPClient.setControlKeepAliveReplyTimeout(1000*60*5);
            mFTPClient.setConnectTimeout(1000*30);
            if(mFTPClient.isConnected()){
                mResponseData.setmStatusCode(FilesFTPUtils.CONNECT_FTP_SERVER_SUCCESS);
                mResponseData.setmData(mFTPClient.getReplyString());
            }
        } catch (IOException e) {
            LoggerUtil.e("MainApplication", "connectFTPServer()", e.getMessage());
            mResponseData.setmStatusCode(FilesFTPUtils.CONNECT_FTP_SERVER_FAILED);
            mResponseData.setmData(e.getMessage());
            disConnectFTPServer();
        }
        return mResponseData;
    }

    public void logoutFTPServer(){
        if(mFTPClient != null && mFTPClient.isConnected()){
            try {
                mFTPClient.logout();
                mFTPClient.disconnect();
            } catch (IOException e) {
                LoggerUtil.e("MainApplication", "logoutFTPServer()", e.getMessage());
            }
        }
    }

    public void disConnectFTPServer(){
        if(mFTPClient != null && mFTPClient.isConnected()) {
            try {
                mFTPClient.disconnect();
            } catch (IOException e) {
                LoggerUtil.e("MainApplication", "logoutFTPServer()", e.getMessage());
            }
        }
    }


    private FTPFile[] getRemoteFiles(String path){
        if(isFTPConnected()){
            try {
                if(path.equalsIgnoreCase(""))
                    return mFTPClient.listFiles();
                else
                    return mFTPClient.listFiles(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void executeConnect(String mServer, int mPort, String mUsername, String mPassword, ResponseInterface responseInterface){
        HashMap<String, Object> mapData = new HashMap<>();
        mapData.put("host", mServer);
        mapData.put("port", mPort);
        mapData.put("username", mUsername);
        mapData.put("password", mPassword);
        RequestFTPAsyncTask task = new RequestFTPAsyncTask(REQUEST_CONNECT, mapData, responseInterface);
        task.execute((Void) null);
    }

    public void executeGetFiles(String remotePath, ResponseInterface responseInterface){
        HashMap<String, Object> mapData = new HashMap<>();
        mapData.put("remote_path", remotePath);
        RequestFTPAsyncTask task = new RequestFTPAsyncTask(REQUEST_GET_FILES, mapData, responseInterface);
        task.execute((Void) null);
    }


    public void executeDownload(String mLocalDir, String[] mRemoteFilePaths, String[] mLocalFiles, ResponseInterface responseInterface){
        File localFile = Utilities.getOutputFile(DIR_TRANSFER, mLocalDir);
        if(localFile == null){
            responseInterface.processResponseData(new ResponseData(TRANSFER_FILE_FAILED, "Cannot create directory"));
            return;
        }
        HashMap<String, Object> mapData = new HashMap<>();
        mapData.put("local_dir", mLocalDir);
        mapData.put("remote_files", mRemoteFilePaths);
        mapData.put("local_files", mLocalFiles);

        RequestFTPAsyncTask task = new RequestFTPAsyncTask(REQUEST_DOWNLOAD, mapData, responseInterface);
        task.execute((Void) null);
    }

    public void executeUpload(String mRemoteDir, String[] mRemoteFiles, String[] mLocalFilePaths, ResponseInterface responseInterface){
        HashMap<String, Object> mapData = new HashMap<>();
        mapData.put("remote_dir", mRemoteDir);
        mapData.put("remote_files", mRemoteFiles);
        mapData.put("local_files", mLocalFilePaths);
        RequestFTPAsyncTask task = new RequestFTPAsyncTask(REQUEST_UPLOAD, mapData, responseInterface);
        task.execute((Void) null);

    }

    private class RequestFTPAsyncTask extends AsyncTask<Void, Void, ResponseData> {

        private String mRequestType;
        private ResponseInterface mResponseInterface;
        private HashMap<String, Object> mDictDataTransfer;

        public RequestFTPAsyncTask(String mRequestType, HashMap<String, Object> mDictDataTransfer, ResponseInterface mResponseInterface) {
            this.mRequestType = mRequestType;
            this.mResponseInterface = mResponseInterface;
            this.mDictDataTransfer = mDictDataTransfer;
        }

        @Override
        protected ResponseData doInBackground(Void... voids) {
            ResponseData responseData;
            switch (mRequestType){
                case REQUEST_UPLOAD:
                    LoggerUtil.d("RequestFTPAsyncTask", "doInBackground()", "Upload files");
                    String remoteDir = (String) mDictDataTransfer.get("remote_dir");
                    String[] remoteFiles = (String[]) mDictDataTransfer.get("remote_files");
                    String[] localPaths = (String[]) mDictDataTransfer.get("local_files");
                    responseData = uploadFiles(remoteDir, remoteFiles, localPaths);
                    break;
                case REQUEST_CONNECT:
                    LoggerUtil.d("RequestFTPAsyncTask", "doInBackground()", "Connect to server");
                    String host = (String) mDictDataTransfer.get("host");
                    int port = (int) mDictDataTransfer.get("port");
                    String username = (String) mDictDataTransfer.get("username");
                    String password = (String) mDictDataTransfer.get("password");
                    responseData = connectFTPServer(host, port, username, password);
                    break;
                case REQUEST_GET_FILES:
                    LoggerUtil.d("RequestFTPAsyncTask", "doInBackground()", "Get files list");
                    String remoteFilePath = (String) mDictDataTransfer.get("remote_path");
                    FTPFile[] ftpFiles = getRemoteFiles(remoteFilePath);
                    if(ftpFiles != null){
                        List<FTPFile> ftpFileList = Arrays.asList(ftpFiles);
                        responseData = new ResponseData(TRANSFER_FILE_SUCCESS, "", ftpFileList);
                    }else {
                        responseData = new ResponseData(TRANSFER_FILE_FAILED, "Cannot get remote files");
                    }
                    break;
                case REQUEST_DOWNLOAD:
                    LoggerUtil.d("RequestFTPAsyncTask", "doInBackground()", "Download files");
                    String localDir = (String) mDictDataTransfer.get("local_dir");
                    String[] remotePaths = (String[]) mDictDataTransfer.get("remote_files");
                    String[] localFiles = (String[]) mDictDataTransfer.get("local_files");
                    responseData = downloadAndSaveFile(localDir, remotePaths, localFiles);
                    break;
                default:
                    responseData = new ResponseData(-1, "No action");
                    break;
            }
            return responseData;
        }

        @Override
        protected void onPostExecute(ResponseData responseData) {
            mResponseInterface.processResponseData(responseData);
        }
    }

    private ResponseData downloadAndSaveFile(String localDir, String[] remoteFilesPath, String[] localFiles) {
        ResponseData responseData = new ResponseData();
        if(mFTPClient != null && mFTPClient.isConnected()){
            try {
                int length;
                if(remoteFilesPath.length < localFiles.length)
                    length = remoteFilesPath.length;
                else
                    length = localFiles.length;
                int numSuccess = length;
                LoggerUtil.d("FilesFTPUtils", "downloadAndSaveFile()", localDir + "; " + remoteFilesPath[0] + "; " + localFiles[0]);
                for(int i=0; i<length; i++){
                    File f = Utilities.getOutputFile(localDir, localFiles[i]);
                    LoggerUtil.d("FilesFTPUtils", "downloadAndSaveFile()", f + "");
                    if(f == null){
                        numSuccess--;
                        continue;
                    }
                    OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(f));
                    boolean success = mFTPClient.retrieveFile(remoteFilesPath[i], outputStream);
                    outputStream.close();
                    if(!success)
                        numSuccess--;
                }
                if (numSuccess == length) {
                    responseData.setmStatusCode(TRANSFER_FILE_SUCCESS);
                } else if(numSuccess > 0){
                    responseData.setmStatusCode(TRANSFER_FILE_SUCCESS);
                    responseData.setmData("Some files could not download");
                }else {
                    responseData.setmStatusCode(TRANSFER_FILE_SUCCESS);
                    responseData.setmData("Could not download");
                }
            } catch (IOException e) {
                LoggerUtil.e("FilesFTPUtils", "downloadAndSaveFile()", e.getMessage());
                responseData.setmStatusCode(TRANSFER_FILE_FAILED);
                responseData.setmData(e.getMessage());
                logoutFTPServer();
            }
        }else {
            responseData.setmStatusCode(TRANSFER_FILE_FAILED);
            responseData.setmData("FTP Server is not connected");
        }

        return responseData;
    }

    private ResponseData uploadFiles(String remoteDir, String[] remoteFiles, String[] localFilesPath) {
        ResponseData responseData = new ResponseData();
        if(mFTPClient != null && mFTPClient.isConnected()){
            try {
                int length;
                if(remoteFiles.length < localFilesPath.length)
                    length = remoteFiles.length;
                else
                    length = localFilesPath.length;
                int numSuccess = length;
                FTPFile[] listDir = mFTPClient.listDirectories();
                boolean isCreated = false;
                for(FTPFile ftpFile : listDir){
                    if(ftpFile.getName().equalsIgnoreCase(remoteDir)){
                        isCreated = true;
                        break;
                    }
                }
                if(!isCreated){
                    mFTPClient.makeDirectory(remoteDir);
                }
                mFTPClient.changeWorkingDirectory(remoteDir);
                LoggerUtil.d("FilesFTPUtils", "downloadAndSaveFile()", remoteDir + "; " + remoteFiles[0] + "; " + localFilesPath[0]);
                for(int i=0; i<length; i++){
                    File f = new File(localFilesPath[i]);
                    InputStream inputStream = new FileInputStream(f);
                    boolean success = mFTPClient.storeFile(remoteFiles[i], inputStream);
                    inputStream.close();
                    if(!success)
                        numSuccess--;
                }
                if (numSuccess == length) {
                    responseData.setmStatusCode(TRANSFER_FILE_SUCCESS);
                    responseData.setmData("Upload files successfully");
                } else if(numSuccess > 0){
                    responseData.setmStatusCode(TRANSFER_FILE_SUCCESS);
                    responseData.setmData("Some files could not upload");
                }else {
                    responseData.setmStatusCode(TRANSFER_FILE_SUCCESS);
                    responseData.setmData("Could not upload");
                }
            } catch (IOException e) {
                LoggerUtil.e("FilesFTPUtils", "uploadFiles()", e.getMessage());
                responseData.setmStatusCode(TRANSFER_FILE_FAILED);
                responseData.setmData(e.getMessage());
                logoutFTPServer();
            }
        }else {
            responseData.setmStatusCode(TRANSFER_FILE_FAILED);
            responseData.setmData("FTP Server is not connected");
        }

        return responseData;
    }

}
