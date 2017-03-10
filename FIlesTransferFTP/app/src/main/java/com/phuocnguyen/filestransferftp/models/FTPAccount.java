package com.phuocnguyen.filestransferftp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by phuocnguyen on 10 Mar 2017.
 */

public class FTPAccount implements Parcelable {
    private int mId;
    private String mHost;
    private int mPort;
    private String mUsername;
    private String mPassword;

    public FTPAccount() {
    }

    public FTPAccount(int mId, String mHost, int mPort, String mUsername, String mPassword) {
        this.mId = mId;
        this.mHost = mHost;
        this.mPort = mPort;
        this.mUsername = mUsername;
        this.mPassword = mPassword;
    }

    protected FTPAccount(Parcel in) {
        mId = in.readInt();
        mHost = in.readString();
        mPort = in.readInt();
        mUsername = in.readString();
        mPassword = in.readString();
    }

    public static final Creator<FTPAccount> CREATOR = new Creator<FTPAccount>() {
        @Override
        public FTPAccount createFromParcel(Parcel in) {
            return new FTPAccount(in);
        }

        @Override
        public FTPAccount[] newArray(int size) {
            return new FTPAccount[size];
        }
    };

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmHost() {
        return mHost;
    }

    public void setmHost(String mHost) {
        this.mHost = mHost;
    }

    public int getmPort() {
        return mPort;
    }

    public void setmPort(int mPort) {
        this.mPort = mPort;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    @Override
    public String toString() {
        return "{Host: " + mHost + "; Port: " + mPort +
                "; Username: " + mUsername + "; Password: " + mPassword + "}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mHost);
        dest.writeInt(mPort);
        dest.writeString(mUsername);
        dest.writeString(mPassword);
    }
}
