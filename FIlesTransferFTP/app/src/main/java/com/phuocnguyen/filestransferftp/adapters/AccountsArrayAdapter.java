package com.phuocnguyen.filestransferftp.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.phuocnguyen.filestransferftp.R;
import com.phuocnguyen.filestransferftp.models.FTPAccount;

import java.util.List;

/**
 * Created by phuocnguyen on 10 Mar 2017.
 */

public class AccountsArrayAdapter  extends ArrayAdapter<FTPAccount> {

    private Activity mActivity;
    private int mLayoutId;
    private LayoutInflater mInflater;
    private List<FTPAccount> mAccountsList;

    public AccountsArrayAdapter(Context context, int resource, List<FTPAccount> objects) {
        super(context, resource, objects);
        this.mAccountsList = objects;
        this.mActivity = (Activity) context;
        this.mLayoutId = resource;
        this.mInflater = this.mActivity.getLayoutInflater();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = mInflater.inflate(mLayoutId, null);
            viewHolder = new ViewHolder();
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.item_acc_tv_username);
            viewHolder.tvHost = (TextView) convertView.findViewById(R.id.item_acc_tv_host);
            viewHolder.tvPort = (TextView) convertView.findViewById(R.id.item_acc_tv_port);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String username = mActivity.getString(R.string.act_acc_editor_username_label) + ": "
                + mAccountsList.get(position).getmUsername();
        viewHolder.tvUsername.setText(username);
        String host = mActivity.getString(R.string.act_acc_editor_host_label) + ": "
                + mAccountsList.get(position).getmHost();
                viewHolder.tvHost.setText(host);
        String strPort = mActivity.getString(R.string.act_acc_editor_port_label) + ": "
                + mAccountsList.get(position).getmPort();
        viewHolder.tvPort.setText(strPort);
        return convertView;
    }


    private static class ViewHolder{
        TextView tvUsername;
        TextView tvHost;
        TextView tvPort;
    }
}
