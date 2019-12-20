package club.qixqi.uiqq;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import club.qixqi.uiqq.bean.FileLink;
import club.qixqi.uiqq.bean.User;
import club.qixqi.uiqq.util.HttpUtil;
import club.qixqi.uiqq.util.SharedPreferenceUtil;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileUploadActivity extends AppCompatActivity implements View.OnClickListener {

    private User selfUser;
    private FileLink fileLink;             // 当前目录

    private static final int CHOOSE_PHOTO = 1;
    private static final int CHOOSE_VIDEO = 2;
    private static final int CHOOSE_DOCUMENT = 3;
    private static final int CHOOSE_AUDIO = 4;
    private static final int CHOOSE_OTHER = 5;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);

        // 获取当前登录用户
        selfUser = SharedPreferenceUtil.getLoginUser(FileUploadActivity.this);
        setFileLink();

        initView();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    /**
     * 初始化视图和监听器绑定
     */
    private void initView() {
        ImageView uploadPicture = (ImageView) findViewById(R.id.upload_picture);
        TextView uploadPictureText = (TextView) findViewById(R.id.upload_picture_text);
        ImageView uploadVideo = (ImageView) findViewById(R.id.upload_video);
        TextView uploadVideoText = (TextView) findViewById(R.id.upload_video_text);
        ImageView uploadDocument = (ImageView) findViewById(R.id.upload_document);
        TextView uploadDocText = (TextView) findViewById(R.id.upload_document_text);
        uploadPicture.setOnClickListener(this);
        uploadPictureText.setOnClickListener(this);
        uploadVideo.setOnClickListener(this);
        uploadVideoText.setOnClickListener(this);
        uploadDocument.setOnClickListener(this);
        uploadDocText.setOnClickListener(this);

        ImageView uploadAudio = (ImageView) findViewById(R.id.upload_audio);
        TextView uploadAudioText = (TextView) findViewById(R.id.upload_audio_text);
        ImageView newFolder = (ImageView) findViewById(R.id.upload_new_folder);
        TextView newFolderText = (TextView) findViewById(R.id.upload_new_folder_text);
        ImageView uploadOther = (ImageView) findViewById(R.id.upload_other);
        TextView uploadOtherText = (TextView) findViewById(R.id.upload_other_text);
        uploadAudio.setOnClickListener(this);
        uploadAudioText.setOnClickListener(this);
        newFolder.setOnClickListener(this);
        newFolderText.setOnClickListener(this);
        uploadOther.setOnClickListener(this);
        uploadOtherText.setOnClickListener(this);
    }


    private void setFileLink(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null && bundle.containsKey("fileLink")){
            fileLink = (FileLink) bundle.getSerializable("fileLink");
        }else{
            Toast.makeText(this, "未获取到当前目录信息", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_file_upload, menu);
        return true;
    }


    /**
     * 点击事件处理
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_picture:
            case R.id.upload_picture_text:
                // Toast.makeText(this, "上传图片", Toast.LENGTH_SHORT).show();
                if (ContextCompat.checkSelfPermission(FileUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(FileUploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                break;
            case R.id.upload_video:
            case R.id.upload_video_text:
                // Toast.makeText(this, "上传视频", Toast.LENGTH_SHORT).show();
                if(ContextCompat.checkSelfPermission(FileUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(FileUploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else{
                    openVideo();
                }
                break;
            case R.id.upload_document:
            case R.id.upload_document_text:
                // Toast.makeText(this, "上传文档", Toast.LENGTH_SHORT).show();
                if(ContextCompat.checkSelfPermission(FileUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(FileUploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                } else{
                    openDocument();
                }
                break;
            case R.id.upload_audio:
            case R.id.upload_audio_text:
                // Toast.makeText(this, "上传音乐", Toast.LENGTH_SHORT).show();
                if(ContextCompat.checkSelfPermission(FileUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(FileUploadActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
                } else{
                    openAudio();
                }
                break;
            case R.id.upload_new_folder:
            case R.id.upload_new_folder_text:
                // Toast.makeText(this, "新建文件夹", Toast.LENGTH_SHORT).show();
                final EditText inputFolderName = new EditText(FileUploadActivity.this);
                AlertDialog.Builder dialog = new AlertDialog.Builder(FileUploadActivity.this);
                dialog.setTitle("新建文件夹");
                dialog.setView(inputFolderName);
                dialog.setPositiveButton("创建", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(FileUploadActivity.this, inputFolderName.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.upload_other:
            case R.id.upload_other_text:
                // Toast.makeText(this, "上传其他", Toast.LENGTH_SHORT).show();
                if(ContextCompat.checkSelfPermission(FileUploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(FileUploadActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5);
                } else{
                    openOther();
                }
                break;
            default:
        }
    }

    /**
     * 顶部导航栏选择事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_close:
                finish();
                break;
            default:
        }
        return true;
    }



    /**
     * 发起网络请求，创建文件夹
     * @param folderName
     */
    private void createNewFolder(String folderName){
        String address = "https://www.ourvultr.club:8443/qq/FileUpload";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", Integer.toString(selfUser.getUserId()))
                .add("parent", Integer.toString(fileLink.getLinkId()))
                .add("folderName", folderName)
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                showResponse(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseMsg = response.body().string();
                showResponse(responseMsg);
            }
        });

    }



    /**
     * 根据真实文件路径上传文件到服务器
     * @param filePath
     */
    private void uploadFile(String filePath) {
        File file = new File(filePath);
        String address = "https://www.ourvultr.club:8443/qq/FileUpload";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId", Integer.toString(selfUser.getUserId()))
                .addFormDataPart("parent", Integer.toString(fileLink.getLinkId()))
                .addFormDataPart("file", filePath, RequestBody.create(MediaType.parse("multipart/form-data"), new File(filePath)))
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                showResponse(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                showResponse(response.body().string());
            }
        });

    }

    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FileUploadActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 检查权限
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openVideo();
                }else{
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openDocument();
                }else{
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            case 4:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAudio();
                } else{
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            case 5:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openOther();
                } else{
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }


    /**
     * Intent 响应结果分析
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 4.4 及以上版本需要解析文件真实路径
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;

            case CHOOSE_VIDEO:
                if(resultCode == RESULT_OK){
                    handleVideoOnKitKat(data);
                }else{
                    handleVideoBeforeKitKat(data);
                }
                break;

            case CHOOSE_DOCUMENT:
                if(resultCode == RESULT_OK){
                    Toast.makeText(this, data.toString(), Toast.LENGTH_SHORT).show();
                }
                break;

            case CHOOSE_AUDIO:
                if(resultCode == RESULT_OK){
                    if(Build.VERSION.SDK_INT >= 19){
                        handleAudioOnKitKat(data);
                    }else{
                        handleAudioBeforeKitKat(data);
                    }
                }
                break;

            case CHOOSE_OTHER:
                if(resultCode == RESULT_OK){
                    Toast.makeText(this, data.toString(), Toast.LENGTH_SHORT).show();
                }
                break;

            default:
        }
    }

    /**
     * 打开图片库
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document的uri，则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];        // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的uri, 则普通处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的uri，直接获取图片路径
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }


    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        // 通过uri和selection来获取图片真实路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    private void displayImage(String imagePath){
        if(imagePath != null){
            Toast.makeText(this, "获取图片成功: " + imagePath, Toast.LENGTH_SHORT).show();
            uploadFile(imagePath);
        } else{
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 打开音乐库
     */
    private void openAudio(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("audio/*");
        startActivityForResult(intent, CHOOSE_AUDIO);
    }

    @TargetApi(19)
    private void handleAudioOnKitKat(Intent data){
        String audioPath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this, uri)){
            // 如果是document的uri，则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];        // 解析出数字格式的id
                String selection = MediaStore.Audio.Media._ID + "=" + id;
                audioPath = getAudioPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selection);
            } else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                audioPath = getAudioPath(contentUri, null);
            }
        } else if("content".equalsIgnoreCase(uri.getScheme())){
            // 如果是content类型的uri, 则普通处理
            audioPath = getAudioPath(uri, null);
        } else if("file".equalsIgnoreCase(uri.getScheme())){
            // 如果是file类型的uri，直接获取图片路径
            audioPath = uri.getPath();
        }
        displayAudio(audioPath);    // 根据图片路径显示图片
    }

    private void handleAudioBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String audioPath = getAudioPath(uri, null);
        displayAudio(audioPath);
    }

    private String getAudioPath(Uri uri, String selection){
        String path = null;
        // 通过uri和selection来获取音乐真实路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayAudio(String audioPath){
        if(audioPath != null){
            Toast.makeText(this, "获取音乐成功: " + audioPath, Toast.LENGTH_SHORT).show();
            uploadFile(audioPath);
        } else{
            Toast.makeText(this, "获取音乐失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 从文档库中获取文档
     */
    private void openDocument(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain;application/msword;application/vnd.openxmlformats-officedocument.wordprocessingml.document;application/vnd.ms-excel;" +
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;application/pdf;application/vnd.ms-powerpoint;" +
                "application/vnd.openxmlformats-officedocument.presentationml.presentation;");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, CHOOSE_DOCUMENT);
    }


    /**
     * 从视频库中获取视频
     */
    private void openVideo(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("video/*");
        startActivityForResult(intent, CHOOSE_VIDEO);
    }

    @TargetApi(19)
    private void handleVideoOnKitKat(Intent data){
        String videoPath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this, uri)){
            // 如果是document的uri，则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];        // 解析出数字格式的id
                String selection = MediaStore.Video.Media._ID + "=" + id;
                videoPath = getVideoPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, selection);
            } else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                videoPath = getVideoPath(contentUri, null);
            }
        } else if("content".equalsIgnoreCase(uri.getScheme())){
            // 如果是content类型的uri, 则普通处理
            videoPath = getVideoPath(uri, null);
        } else if("file".equalsIgnoreCase(uri.getScheme())){
            // 如果是file类型的uri，直接获取图片路径
            videoPath = uri.getPath();
        }
        displayVideo(videoPath);    // 根据图片路径显示图片
    }


    private void handleVideoBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String videoPath = getVideoPath(uri, null);
        displayVideo(videoPath);
    }

    private String getVideoPath(Uri uri, String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayVideo(String videoPath){
        if(videoPath != null){
            Toast.makeText(this, "获取视频成功: " + videoPath, Toast.LENGTH_SHORT).show();
            uploadFile(videoPath);
        }else{
            Toast.makeText(this, "获取视频失败", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 从内部存储中获取文件
     */
    private void openOther(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, CHOOSE_OTHER);
    }



}
