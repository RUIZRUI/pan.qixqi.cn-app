package club.qixqi.uiqq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.sql.SQLException;

import club.qixqi.uiqq.bean.User;
import club.qixqi.uiqq.services.DownloadService;
import club.qixqi.uiqq.util.SharedPreferenceUtil;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * todo
 * 1. 弹出的通知不对啊
 */


public class FileDownloadActivity extends AppCompatActivity implements View.OnClickListener{

    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };



    private User selfUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);

        // 获取当前登录用户
        selfUser = SharedPreferenceUtil.getLoginUser(FileDownloadActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        changeHomeAsUp(actionBar);

        // 开启DownloadService
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);

        // 权限检查
        if(ContextCompat.checkSelfPermission(FileDownloadActivity.this, Manifest.permission.START_VIEW_PERMISSION_USAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FileDownloadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        Button test = (Button) findViewById(R.id.test);
        test.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toolbar_file_download, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.test:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            // String url = "https://www.ourvultr.club:8443/qq/upload/files/0a7aaa28bde6d2c067ce18bebd6774bc.mp4";
                            String url = "https://www.ourvultr.club:8443/qq/FileDownload";
                            OkHttpClient client = new OkHttpClient();
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("method", "getSize")
                                    .add("userId", Integer.toString(801935))
                                    .add("password", selfUser.getPassword())
                                    .add("linkId", Integer.toString(9202582))
                                    .build();
                            Request request = new Request.Builder()
                                    .url(url)
                                    .post(requestBody)
                                    .build();
                            Response response = client.newCall(request).execute();
                            if (response != null && response.isSuccessful()) {
                                long contentLength = response.body().contentLength();
                                long length = Long.parseLong(response.body().string().trim());  // 去除最后一个换行符号
                                // String length = response.body().string();
                                response.body().close();
                                // return contentLength;
                                Log.e("FileDownload1", Long.toString(length));
                                Log.e("FileDownload1", Long.toString(contentLength));
                                // Toast.makeText(this, Long.toString(contentLength), Toast.LENGTH_SHORT).show();
                            }
                            // return 0;
                            // Toast.makeText(this, "0", Toast.LENGTH_SHORT).show();
                            Log.e("FileDownload1", "0");
                        } catch (IOException e){
                            // Log.e("FileDownload", e.getMessage());
                            e.printStackTrace();
                            Log.e("FileDownload1", "nihao");
                            if(e.getMessage()==null){
                                Log.e("FileDownload1", "o");
                            }
                            // Toast.makeText(this, "exception", Toast.LENGTH_SHORT).show();
                            Log.e("FileDownload1", "exception");
                        }

                    }
                }).start();


                break;
            default:
        }
    }

    /**
     * 顶部导航栏选择监听器
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.nav_more:
                // String url = "https://www.ourvultr.club:8443/qq/upload/files/0a7aaa28bde6d2c067ce18bebd6774bc.mp4";
                String url = "https://www.ourvultr.club:8443/qq/FileDownload";
                String linkIdStr = Integer.toString(9202582);
                downloadBinder.startDownload(url, linkIdStr);
                break;
            default:
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "拒绝权限", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    /**
     * 修改HomeAsUp图标
     * @param actionBar
     */
    private void changeHomeAsUp(ActionBar actionBar){
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

}
