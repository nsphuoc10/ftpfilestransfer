package com.phuocnguyen.filestransferftp.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.phuocnguyen.filestransferftp.R;
import com.phuocnguyen.filestransferftp.utils.Utilities;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.util.List;

/**
 * Created by phuocnguyen on 8 Mar 2017.
 */

public class RemoteFilesArrayAdapter extends ArrayAdapter<FTPFile>{


    private List<FTPFile> mListFilesTransfer;
    private Activity mActivity;
    private int mLayoutId;
    private LayoutInflater mInflater;

    public RemoteFilesArrayAdapter(Context context, int resource, List<FTPFile> objects) {
        super(context, resource, objects);
        this.mListFilesTransfer = objects;
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
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.item_file_iv_icon);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.item_file_tv_title);
            viewHolder.tvSize = (TextView) convertView.findViewById(R.id.item_file_tv_size);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.item_file_tv_date);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FTPFile fileTransfer =  mListFilesTransfer.get(position);

        viewHolder.tvTitle.setText(fileTransfer.getName());
        viewHolder.tvDate.setText(fileTransfer.getTimestamp().getTime().toString());

        if(fileTransfer.isDirectory()){
            viewHolder.tvSize.setText(mActivity.getString(R.string.item_file_folder));
            viewHolder.ivIcon.setImageResource(R.drawable.ic_folder);
        }else {
            viewHolder.tvSize.setText(Utilities.parseSize(fileTransfer.getSize()));
            viewHolder.ivIcon.setImageResource(Utilities.getIconViaExtension(mActivity,
                    Utilities.getExtensionFromFile(fileTransfer)));
        }
        return convertView;
    }

    private static class ViewHolder{
        TextView tvTitle;
        TextView tvSize;
        TextView tvDate;
        ImageView ivIcon;
    }
}
