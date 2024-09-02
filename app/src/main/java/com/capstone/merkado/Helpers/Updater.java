package com.capstone.merkado.Helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;

public class Updater {

    public static void allowDownload(Activity activity) {
            activity.startActivity(
                    new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            Uri.parse("package:" + activity.getPackageName())));
    }

    public static void updateApp(Context context, String apkUrl) {
        Uri uri = Uri.parse(apkUrl);
        String fileName = uri.getLastPathSegment();

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("Downloading update");
        request.setDescription("Updating the app...");
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        apkInstaller(context, downloadManager, downloadId);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private static void apkInstaller(Context context, DownloadManager downloadManager, Long downloadId) {
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Uri uri = downloadManager.getUriForDownloadedFile(downloadId);

                if (uri != null) {
                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                    installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    // For Android 7.0 and above
                    installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(uri.getPath()));

                    context.startActivity(installIntent);
                }

                context.unregisterReceiver(this);
            }
        };

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(onComplete, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            context.registerReceiver(onComplete, filter);
        }
    }

}