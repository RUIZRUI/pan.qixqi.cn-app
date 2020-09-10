package cn.qixqi.pan.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import cn.qixqi.pan.FileDownloadActivity;
import cn.qixqi.pan.R;
import cn.qixqi.pan.dao.FileTransferDao;
import cn.qixqi.pan.listeners.DownloadListener;
import cn.qixqi.pan.tasks.DownloadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DownloadService extends Service {

    private DownloadTask downloadTask;
    private String downloadUrl;
    private FileTransferDao fileTransferDao = null;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // private Callback callback;


    /**
     * 自定义接口
     */
    /* public interface Callback{
        public void uploadDownloaded();
    }*/


    @Override
    public void onCreate() {
        super.onCreate();
        fileTransferDao = new FileTransferDao(DownloadService.this);
    }



    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1, getNotification("Downloading ...", progress));
        }

        @Override
        public void onSuccess(int linkId) {
            downloadTask = null;
            // 下载成功后将前台服务通知关闭，并创建一个新的下载成功的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Success", -1));    // -1不显示进度
            Toast.makeText(DownloadService.this, "Download Success", Toast.LENGTH_SHORT).show();
            fileTransferDao.editStatus(linkId, 0, 'h');
            fileTransferDao.editFinishTime(linkId, 0, df.format(new Date()));
            // callback.uploadDownloaded();
            // Log.d("hello", linkId + "");
            // Log.d("hello" ,  Boolean.toString(fileTransferDao.isContain(linkId)));
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            // 下载失败将前台服务通知关闭，并创建一个新的下载失败的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Failed", -1));
            Toast.makeText(DownloadService.this, "Download Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask = null;
            Toast.makeText(DownloadService.this, "Download Paused", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "Download Canceled", Toast.LENGTH_SHORT).show();
        }
    };



    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
    }

    public class DownloadBinder extends Binder{
        public void startDownload(String url, String linkIdStr){
            downloadUrl = url;
            String [] params = {url, linkIdStr};
            downloadTask = new DownloadTask(listener);
            downloadTask.execute(params);
            Log.d("Download", "hello");
            startForeground(1, getNotification("Downloading...", 0));
            // getNotificationManager().notify(1, getNotification("Downloading...", 0));
            Log.d("Download", "world");
            Toast.makeText(DownloadService.this, "Downloading ...", Toast.LENGTH_SHORT).show();
        }

        public void pauseDownload(){
            if(downloadTask != null){
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownload(){
            if(downloadTask != null){
                downloadTask.cancelDownload();
            }
            if(downloadUrl != null){
                // 取消下载删除文件，并将通知关闭
//                String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
//                String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                String directory = Environment.getExternalStorageDirectory().toString() + "/qixqi/";
                String nickName = downloadTask.getNickName();
                File file = new File(directory + nickName);
                if(file.exists()){
                    file.delete();
                }
                getNotificationManager().cancel(1);
                stopForeground(true);
                Toast.makeText(DownloadService.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }




    public DownloadService() {
    }




    private NotificationManager getNotificationManager(){
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress){
        Intent intent = new Intent(this, FileDownloadActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("download", "download content", NotificationManager.IMPORTANCE_HIGH);
            getNotificationManager().createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, "download");
        }else{
            builder = new NotificationCompat.Builder(this);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        if(progress >= 0){
            // 当progress 大于或等于0时才需要显示下载进度
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }
}
