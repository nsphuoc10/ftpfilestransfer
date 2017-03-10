package com.phuocnguyen.filestransferftp.models;

import org.apache.commons.net.ftp.FTPFile;

import java.util.List;

/**
 * Created by phuocnguyen on 3/8/2017.
 */

public class ResponseData {
    private int mStatusCode;
    private String mData;
    private List<FTPFile> mFileTransferList;

    public ResponseData() {
    }

    public ResponseData(int mStatusCode, String mData) {
        this.mStatusCode = mStatusCode;
        this.mData = mData;
    }

    public ResponseData(int mStatusCode, String mData, List<FTPFile> mFileTransferList) {
        this.mStatusCode = mStatusCode;
        this.mData = mData;
        this.mFileTransferList = mFileTransferList;
    }

    public int getmStatusCode() {
        return mStatusCode;
    }

    public void setmStatusCode(int mStatusCode) {
        this.mStatusCode = mStatusCode;
    }

    public String getmData() {
        return mData;
    }

    public void setmData(String mData) {
        this.mData = mData;
    }

    public List<FTPFile> getmFileTransferList() {
        return mFileTransferList;
    }

    public void setmFileTransferList(List<FTPFile> mFileTransferList) {
        this.mFileTransferList = mFileTransferList;
    }

    @Override
    public String toString() {
        return "StatusCode: " + mStatusCode + "; Data: " + mData + "; listFile: " + mFileTransferList;
    }
}
