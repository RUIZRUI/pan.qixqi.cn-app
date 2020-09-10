package cn.qixqi.pan.tasks;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URLDecoder;

import cn.qixqi.pan.bean.User;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import cn.qixqi.pan.listeners.DownloadListener;
import cn.qixqi.pan.context.MyApplication;
import cn.qixqi.pan.util.SharedPreferenceUtil;

public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;

    public String nickName;

    private User selfUser = SharedPreferenceUtil.getLoginUser(MyApplication.getContext());
    private int linkId;

    private DownloadListener listener;

    private boolean isCanceled = false;

    private boolean isPaused = false;

    private int lastProgress;

    public DownloadTask(DownloadListener listener){
        this.listener = listener;
    }


    @Override
    protected void onPostExecute(Integer status) {
        switch (status){
            case TYPE_SUCCESS:
                listener.onSuccess(linkId);
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            default:
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if(progress > lastProgress){
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }

    @Override
    protected Integer doInBackground(String... strings) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;
        try{
            long downloadedLength = 0;      // 已经下载长度
            String downloadUrl = strings[0];
            String linkIdStr = strings[1];
            linkId = Integer.parseInt(linkIdStr);
            Log.d("hello", "linkIdStr = " + linkIdStr);
            Log.d("hello", "linkId = " + linkId);
//            String fileName  = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            // String fileName = "/test";
            // String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            String directory = Environment.getExternalStorageDirectory().toString() + "/qixqi/";
            File directoryFile = new File(directory);
            if(!directoryFile.exists()){
                directoryFile.mkdirs();
            }
            // Log.d("Download", "directory = " + directory);
            // Log.d("Download", "fileName = " + fileName);


            RequestBody requestBody = new FormBody.Builder()
                    .add("method", "download")
                    .add("userId", Integer.toString(selfUser.getUserId()))
                    .add("password", selfUser.getPassword())
                    // .add("linkId", Integer.toString(9202582))
                    .add("linkId", linkIdStr)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    .url(downloadUrl)
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            if(response != null){
                nickName = response.header("content-disposition").substring(new String("attachment;filename=").length());
                nickName = URLDecoder.decode(nickName, "utf-8");
                // Log.e("FileDownload1", nickName);
                file = new File(directory + nickName);
                if(file.exists()){
                    downloadedLength = file.length();
                }
                long contentLength = getContentLength(downloadUrl, linkIdStr);
                if(contentLength == 0){
                    return TYPE_FAILED;
                } else if(contentLength == downloadedLength){
                    return TYPE_SUCCESS;
                }
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadedLength);
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while((len = is.read(b)) != -1){
                    if(isCanceled){
                        return TYPE_CANCELED;
                    }else if(isPaused){
                        return TYPE_PAUSED;
                    }else{
                        total += len;
                        savedFile.write(b, 0, len);
                        // 已经下载百分比
                        int progress = (int)((total + downloadedLength) * 100 / contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }else{
                return TYPE_FAILED;
            }

        }catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                if(is!= null){
                    is.close();
                }
                if(savedFile != null){
                    savedFile.close();
                }
                if(isCanceled && file!=null){
                    file.delete();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return TYPE_FAILED;
    }



    public void pauseDownload(){
        isPaused = true;
    }

    public void cancelDownload(){
        isCanceled = true;
    }

    private long getContentLength(String downloadUrl, String linkIdStr) throws IOException{
        RequestBody requestBody = new FormBody.Builder()
                .add("method", "getSize")
                .add("userId", Integer.toString(selfUser.getUserId()))
                .add("password", selfUser.getPassword())
                // .add("linkId", Integer.toString(9202582))
                .add("linkId", linkIdStr)
                .build();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        if(response != null && response.isSuccessful()){
            // long contentLength = response.body().contentLength();
            long contentLength = Long.parseLong(response.body().string().trim());
            response.body().close();
            return contentLength;
        }
        return 0;
    }


    /**
     * 获取文件名
     * @return
     */
    public String getNickName(){
        return nickName;
    }



}
