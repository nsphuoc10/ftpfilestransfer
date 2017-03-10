package com.phuocnguyen.filestransferftp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;


import com.phuocnguyen.filestransferftp.R;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by phuocnguyen on 3/8/2017.
 */

public class Utilities {

    public static final String ERROR_MESSAGE = "errorMsg";

    private static AlertDialog mDialog;
    private static ProgressDialog mProgressDialog;

    public static HashMap<String, String> parsePathToFileInfo(String path){
        HashMap<String, String> mapDetail = new HashMap<>();
        if(path == null || path.length() == 0){
            mapDetail.put(ERROR_MESSAGE, "Cannot parse because path is empty");
        }
        int indSlash = -1;
        int indDot = -1;
        if(path.contains("/")){
            indSlash = path.lastIndexOf("/");
            mapDetail.put("path", path.substring(0, indSlash+1));
        }

        if(path.contains(".")){
            indDot = path.lastIndexOf(".");
            mapDetail.put("extension", path.substring(indDot, path.length()));
        }

        if(indSlash !=-1 && indDot !=-1){
            mapDetail.put("filename", path.substring(indSlash+1, indDot));
        }else if(indDot != -1){
            mapDetail.put("filename", path.substring(indSlash+1, path.length()));
        }else if(indSlash != -1){
            mapDetail.put("filename", path.substring(0, indDot));
        }else {
            mapDetail.put("filename", path);
        }
        return mapDetail;
    }

    public static void showProgressDialog(Context ctx, String msg){
        mProgressDialog = new ProgressDialog(ctx);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    public static void hideProgressDialog(){
        if(mProgressDialog != null){
            if(mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }


    public static void showThreeQuestion(final Context context,
                                         final String title, final String message, final Runnable r1,
                                         final Runnable r2, String text1, String text2) {
        final Dialog dialog = new Dialog(context);
        if (title != null)
            dialog.setTitle(title);
        else
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_custom_dialog);

        if (message != null) {
            ((TextView) dialog.findViewById(R.id.custom_dialog_tvMessage)).setText(message);
        }
        Button btnEdit = (Button) dialog
                .findViewById(R.id.custom_dialog_btnEdit);
        if (!text1.equals(context.getString(R.string.empty))) {
            btnEdit.setText(text1);
        }
        Button btnDelete = (Button) dialog
                .findViewById(R.id.custom_dialog_btnDelete);
        if (!text2.equals(context.getString(R.string.empty))) {
            btnDelete.setText(text2);
            btnDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    dialog.dismiss();
                    r2.run();
                }
            });
        } else {
            btnDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    dialog.dismiss();
                    showQuestion(context, context.getString(R.string.custom_dialog_remind_delete),
                            r2);
                }
            });
        }
        Button btnCancel = (Button) dialog
                .findViewById(R.id.custom_dialog_btnCancel);

        btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                r1.run();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showQuestion(Context ctx, String msg, final Runnable r1){
        final AlertDialog.Builder alt_bld = new AlertDialog.Builder(ctx);

        TextView tv = new TextView(ctx);
        tv.setText(msg);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(ContextCompat.getColor(ctx, R.color.Black));
        tv.setTextSize(15.0f);
        alt_bld.setView(tv);
        alt_bld.setPositiveButton(ctx.getString(R.string.dialog_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (r1 != null)
                            r1.run();
                        dialog.dismiss();
                        mDialog = null;
                    }
                });
        alt_bld.setNegativeButton(ctx.getString(R.string.dialog_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        mDialog = null;
                    }
                });
        ((Activity) ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                mDialog = alt_bld.create();
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDialog.setCanceledOnTouchOutside(true);
                mDialog.show();
            }
        });
    }

    public static int getIconViaExtension(Context context, String extension){
        if(extension == null){
            return R.drawable.ic_question;
        }
        if(extension.contains(context.getResources().getString(R.string.pdf)))
            return R.drawable.ic_pdf;
        if(extension.contains(context.getResources().getString(R.string.txt)) || extension.equalsIgnoreCase("txt"))
            return R.drawable.ic_txt;
        if(extension.contains(context.getResources().getString(R.string.word)) || extension.equalsIgnoreCase("docx") ||
                extension.equalsIgnoreCase("doc"))
            return R.drawable.ic_word;
        if(extension.contains(context.getResources().getString(R.string.image)) || extension.equalsIgnoreCase("bmp") ||
                extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") ||
                extension.equalsIgnoreCase("png"))
            return R.drawable.ic_image;
        if(extension.contains(context.getResources().getString(R.string.audio)) || extension.equalsIgnoreCase("mp3")
                || extension.equalsIgnoreCase("wav"))
            return R.drawable.ic_audio;
        if(extension.contains(context.getResources().getString(R.string.video)) || extension.equalsIgnoreCase("mp4") ||
                extension.equalsIgnoreCase("webm") || extension.equalsIgnoreCase("flv")
                || extension.equalsIgnoreCase("mov"))
            return R.drawable.ic_video;
        return R.drawable.ic_question;
    }

    public static String parseDateFromMiliSeconds(long time, String... formatters){
        String fmt;
        if(formatters.length == 0){
            fmt = "MM/dd/yyyy";
        }else
            fmt = formatters[0];
        Date lastModified = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(fmt, Locale.getDefault());
        return format.format(lastModified);
    }

    public static String parseSize(long bytes){
        if(bytes >= 1024 && bytes < 1048576){
            return String.format(Locale.getDefault(), "%.3f KB", bytes*1.0/1024);
        }else if(bytes >= 1048576 && bytes < 1073741824){
            return String.format(Locale.getDefault(), "%.3f MB", bytes*1.0/1048576);
        }else if(bytes >= 1073741824 && bytes < Long.parseLong("1099511627776")){
            return String.format(Locale.getDefault(), "%.3f GB", bytes*1.0/1073741824);
        }
        return String.format(Locale.getDefault(), "%.3f B", bytes*1.0);
    }

    public static String getMimeType(Context context, File file) {
        try {
            Uri uri = Uri.fromFile(file);
            String mimeType = null;
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                ContentResolver cr = context.getContentResolver();
                mimeType = cr.getType(uri);
            } else {
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                        .toString());
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        fileExtension.toLowerCase());
            }
            LoggerUtil.d("Utilities", "getMimeType()", mimeType);
            return mimeType;
        }catch (Exception e){
            return "";
        }
    }

    public static String getExtensionFromFile(FTPFile file){
        String fileName = file.getName();
        int indLastDot = fileName.lastIndexOf(".");
        LoggerUtil.d("FilesFTPUtils", "getExtensionFromFile()", fileName + "; " + indLastDot);
        if(indLastDot != -1){
            return fileName.substring(indLastDot+1);
        }

        return "";
    }

    public static void handleOpenAFile(Context context, File file, View view){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri fileUri = Uri.fromFile(file);
            String mimeType = Utilities.getMimeType(context, file);
            intent.setDataAndType(fileUri, mimeType);
            context.startActivity(intent);
        }catch (Exception e){
            LoggerUtil.e("Utilities", "handleOpenAnApp()", e.getMessage());
            Snackbar.make(view, "This file is unsupported", Snackbar.LENGTH_SHORT).show();
        }
    }

    public static File getOutputFile(String folder, String fileName){
        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory(),
                folder);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                LoggerUtil.d("FilesFTPUtils", "getOutputFile()", "Failed to create directory");
                return null;
            }
        }
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return mediaFile;
    }

    public static boolean checkSDCardExternalExisted(){
        return Environment.getExternalStorageDirectory().exists();
    }


    public static void hideSystemUI(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public static boolean checkFTPServerIsConnected(Context ctx, View view){
        if(!FilesFTPUtils.getInstance().isFTPConnected()){
            try {
                if(ctx != null && view != null)
                    Snackbar.make(view, ctx.getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show();
            }catch (Exception e){
                LoggerUtil.d("FilesFTPUtils", "checkFTPServerIsConnected()");
            }
            return false;
        }
        return true;
    }
}
