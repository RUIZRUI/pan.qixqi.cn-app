package cn.qixqi.pan;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.qixqi.pan.adapter.FileDownloadedAdapter;
import cn.qixqi.pan.adapter.FileDownloadingAdapter;
import cn.qixqi.pan.bean.FileLink;
import cn.qixqi.pan.bean.User;
import cn.qixqi.pan.dao.FileTransferDao;
import cn.qixqi.pan.services.DownloadService;
import cn.qixqi.pan.util.SharedPreferenceUtil;


/**
 * todo
 * 1. 弹出的通知不对啊
 */


public class FileDownloadActivity extends AppCompatActivity implements View.OnClickListener,
    FileDownloadingAdapter.Callback, FileDownloadedAdapter.Callback{

    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
            setFileLink();      // 获取fileLink
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };



    private User selfUser;
    private FileLink fileLink;
    // private FileTransferDao fileTransferDao = new FileTransferDao(FileDownloadActivity.this);    // 报错，我觉得是尚未创建，context空指针异常
    private FileTransferDao fileTransferDao;

    private ListView listViewDownloading;
    private List<FileLink> fileDownloadingList = new ArrayList<>();
    private FileDownloadingAdapter downloadingAdapter;

    private ListView listViewDownloaded;
    private List<FileLink> fileDownloadedList = new ArrayList<>();
    private FileDownloadedAdapter downloadedAdapter;

    private ListView listViewUploading;
    private List<FileLink> fileUploadingList = new ArrayList<>();
    private FileDownloadingAdapter uploadingAdapter;

    private ListView listViewUploaded;
    private List<FileLink> fileUploadedList = new ArrayList<>();
    private FileDownloadedAdapter uploadedAdapter;

    private boolean isDownload = true;  // (true)下载列表 or 上传列表(false)

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private LinearLayout layout_file_downloading;
    private LinearLayout layout_file_downloaded;
    private LinearLayout layout_file_uploaded;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);

        // 获取当前登录用户
        selfUser = SharedPreferenceUtil.getLoginUser(FileDownloadActivity.this);

        fileTransferDao = new FileTransferDao(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        changeHomeAsUp(actionBar);

        TextView downloadView = (TextView) findViewById(R.id.download_view);
        TextView uploadView = (TextView) findViewById(R.id.upload_view);
        downloadView.setOnClickListener(this);
        uploadView.setOnClickListener(this);

        // 开启DownloadService
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);

        // 权限检查
        if(ContextCompat.checkSelfPermission(FileDownloadActivity.this, Manifest.permission.START_VIEW_PERMISSION_USAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FileDownloadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        layout_file_downloading = (LinearLayout) findViewById(R.id.layout_file_downloading);
        layout_file_downloaded = (LinearLayout) findViewById(R.id.layout_file_downloaded);
        layout_file_uploaded = (LinearLayout) findViewById(R.id.layout_file_uploaded);


        // 文件下载进行中
        initFileDownloadingList();
        listViewDownloading = (ListView) findViewById(R.id.downloading_list_view);
        downloadingAdapter = new FileDownloadingAdapter(FileDownloadActivity.this, fileDownloadingList, FileDownloadActivity.this);
        listViewDownloading.setAdapter(downloadingAdapter);

        // 文件下载完成
        initFileDownloadedList();
        listViewDownloaded = (ListView) findViewById(R.id.downloaded_list_view);
        downloadedAdapter = new FileDownloadedAdapter(FileDownloadActivity.this, fileDownloadedList, FileDownloadActivity.this);
        listViewDownloaded.setAdapter(downloadedAdapter);

        // 文件上传进行中
        // initFileUploadingList();
        // listViewUploading = (ListView) findViewById(R.id.uploading_list_view);
        // uploadingAdapter = new FileDownloadingAdapter(FileDownloadActivity.this, fileUploadingList, FileDownloadActivity.this);
        // listViewUploading.setAdapter(uploadingAdapter);

        // 文件上传完成
        initFileUploadedList();
        listViewUploaded = (ListView) findViewById(R.id.uploaded_list_view);
        uploadedAdapter = new FileDownloadedAdapter(FileDownloadActivity.this, fileUploadedList, FileDownloadActivity.this);
        listViewUploaded.setAdapter(uploadedAdapter);
    }


    private void initFileDownloadingList(){
        fileDownloadingList = fileTransferDao.getFileTransfer(selfUser.getUserId(), 0, 'i');
    }

    private void initFileDownloadedList(){
        fileDownloadedList = fileTransferDao.getFileTransfer(selfUser.getUserId(), 0, 'h');
    }

    private void initFileUploadingList(){
        fileUploadingList = fileTransferDao.getFileTransfer(selfUser.getUserId(), 1, 'i');
    }

    private void initFileUploadedList(){
        fileUploadedList = fileTransferDao.getFileTransfer(selfUser.getUserId(), 1, 'h');
    }


    /**
     * 从FileActivity获取fileLink
     */
    private void setFileLink(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null && bundle.containsKey("fileLink")){         // 选中文件，点击下载时有效
            fileLink = (FileLink)bundle.getSerializable("fileLink");
            Log.d("FileDownloadActivity1", fileLink.toString());
            String url = this.getString(R.string.domain) + "FileDownload";
            String linkIdStr = Integer.toString(fileLink.getLinkId());
            downloadBinder.startDownload(url, linkIdStr);
            // fileLink.setDownloadStatus('i');
            // fileTransferDao.add(fileLink);
            if(fileTransferDao.isContain(fileLink.getLinkId())){    // 更新
                fileTransferDao.editStatus(fileLink.getLinkId(), 0, 'i');
            } else{     // 添加
                fileLink.setDownloadStatus('i');
                fileTransferDao.add(fileLink);
            }
            initFileDownloadingList();
            downloadingAdapter.notifyDataSetChanged();
        }
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
            case R.id.download_view:
                // Toast.makeText(FileDownloadActivity.this, "下载列表开发中...", Toast.LENGTH_SHORT).show();
                if(!isDownload){
                    layout_file_downloading.setVisibility(View.VISIBLE);
                    layout_file_downloaded.setVisibility(View.VISIBLE);
                    layout_file_uploaded.setVisibility(View.GONE);
                    isDownload = true;
                }
                break;
            case R.id.upload_view:
                // Toast.makeText(FileDownloadActivity.this, "上传列表开发中...", Toast.LENGTH_SHORT).show();
                if(isDownload){
                    layout_file_downloading.setVisibility(View.GONE);
                    layout_file_downloaded.setVisibility(View.GONE);
                    layout_file_uploaded.setVisibility(View.VISIBLE);
                    isDownload = false;
                }
                break;
            default:
        }
    }


    @Override
    public void click(View v) {

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
                // String url = this.getString(R.string.domain) + "upload/files/0a7aaa28bde6d2c067ce18bebd6774bc.mp4";
                // String url = this.getString(R.string.domain) + "FileDownload";
                // String linkIdStr = Integer.toString(4235862);
                // downloadBinder.startDownload(url, linkIdStr);
                Toast.makeText(FileDownloadActivity.this, "更多", Toast.LENGTH_SHORT).show();
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
