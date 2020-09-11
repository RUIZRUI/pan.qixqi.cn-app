package cn.qixqi.pan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.qixqi.pan.adapter.FileLinkAdapter;
import cn.qixqi.pan.bean.FileLink;
import cn.qixqi.pan.bean.User;
import cn.qixqi.pan.context.MyApplication;
import cn.qixqi.pan.util.HttpUtil;
import cn.qixqi.pan.util.SharedPreferenceUtil;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
    NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, ListView.OnItemClickListener,
    ListView.OnItemLongClickListener, FileLinkAdapter.Callback, View.OnClickListener{

    private static final int RESULT_UPLOAD = 1;

    private User selfUser;
    private FileLink fileLink;             // 当前目录
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SwipeRefreshLayout swipeRefresh;
    private EditText inputContent;
    private ListView listView;
    private List<FileLink> fileLinkList = new ArrayList<>();
    private FileLinkAdapter adapter = null;

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private BottomNavigationView bnavFileMenu;
    private boolean isBnavFileMenu = false;     // 底部标题栏是否显示为 bnavFileMenu
    private int checkPosition = -1;             // 选中的下标，目前只有单选
    private ImageView fileCheckView = null;     // 选中的file_check视图



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        // 获取当前用户
        // selfUser = SharedPreferenceUtil.getLoginUser(FileActivity.this);
        isLogined();
        setFileLink();
        // Toast.makeText(this, fileLink.toString(), Toast.LENGTH_SHORT).show();

        inputContent = (EditText) findViewById(R.id.input_content);
        ImageView shareSearch = (ImageView) findViewById(R.id.share_search);
        ImageView shareClean = (ImageView) findViewById(R.id.share_clean);
        shareSearch.setOnClickListener(this);
        shareClean.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bnav_menu);
        bottomNavigationView.setSelectedItemId(R.id.bnav_file);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // 长按ListView选项替换底部标题栏
        bnavFileMenu = (BottomNavigationView) findViewById(R.id.bnav_file_menu);
        bnavFileMenu.setSelectedItemId(R.id.bnav_file_download);
        bnavFileMenu.setOnNavigationItemSelectedListener(this);

        ActionBar actionBar = getSupportActionBar();
        changeHomeAsUp(actionBar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_call);
        navigationView.setNavigationItemSelectedListener(this);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(this);

        initFileLink();
        listView = (ListView) findViewById(R.id.list_view);
        // listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        adapter = new FileLinkAdapter(FileActivity.this, fileLinkList, FileActivity.this);
        listView.setAdapter(adapter);


    }


    /**
     * 判断是否已经登录
     * @return
     */
    private void isLogined(){
        selfUser = SharedPreferenceUtil.getLoginUser(FileActivity.this);
        if(selfUser.getUserId() == -1){
            Intent failedIntent = new Intent(FileActivity.this, login.class);
            failedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(failedIntent);
        }
    }

    private void initFileLink(){
        /* String time = df.format(new Date());
        for(int i=0; i<2; i++) {
            FileLink fileLink1 = new FileLink(1000001, 100000, -1, null, null, -1, 'y', "我的资源", "", "", 'n', 1000000, time);
            FileLink fileLink2 = new FileLink(1000002, 100000, 1000001, "微信.apk", "apk", 1212323, 'n', null, null, null, 'n', 1000000, time);
            FileLink fileLink3 = new FileLink(1000003, 100000, 1000002, "烟火.mp3", "audio", 121232, 'n', null, null, null, 'n', 1000000, time);
            FileLink fileLink4 = new FileLink(1000004, 100000, 1000003, "测试.txt", "document", 1212233, 'n', null, null, null, 'n', 1000000, time);
            FileLink fileLink5 = new FileLink(1000005, 100000, 1000004, "萌酱.png", "picture", 12132, 'n', null, null, null, 'n', 1000000, time);
            FileLink fileLink6 = new FileLink(1000006, 100000, 1000005, "可爱的小猫.zip", "rar", 12112121212L, 'n', null, null, null, 'n', 1000000, time);
            FileLink fileLink7 = new FileLink(1000007, 100000, 1000006, "dear.bms", "unknown", 131, 'n', null, null, null, 'n', 1000000, time);
            FileLink fileLink8 = new FileLink(1000008, 100000, 1000007, "仙剑奇侠传1第一集.mp4", "video", 1212321, 'n', null, null, null, 'n', 1000000, time);
            fileLinkList.add(fileLink1);
            fileLinkList.add(fileLink2);
            fileLinkList.add(fileLink3);
            fileLinkList.add(fileLink4);
            fileLinkList.add(fileLink5);
            fileLinkList.add(fileLink6);
            fileLinkList.add(fileLink7);
            fileLinkList.add(fileLink8);
        }*/
        swipeRefresh.setRefreshing(true);
        getFileLinks(fileLink.getLinkId());
    }



    private void setFileLink(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null && bundle.containsKey("fileLink")){
            fileLink = (FileLink) bundle.getSerializable("fileLink");
        }else{      // 根文件夹
            fileLink = SharedPreferenceUtil.getRootFolder();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_clean:
                inputContent.setText("");
                break;
            case R.id.share_search:
                // Toast.makeText(FileActivity.this, "即将查找", Toast.LENGTH_SHORT).show();
                String address = inputContent.getText().toString().trim();
                String httpsStr = "";
                if (address.length() > 8) {     // 等于8时也闪退，后面必须要跟一个非空字符
                    httpsStr = address.substring(0, 8);
                }
                if("https://".equals(httpsStr)){
                    HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback(){
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e(MyApplication.getContext().getString(R.string.domain), "FileActivity.java: " + e.getMessage());
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("error", e.getMessage());
                            responseSearchShare(jsonObject);
                        }


                        /**
                         * 有问题，如果返回404错误，闪退，所以要try ... catch
                         * @param call
                         * @param response
                         * @throws IOException
                         */
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            JSONObject jsonObject;
                            try {
                                String responseStr = response.body().string().trim();
                                jsonObject = JSON.parseObject(responseStr);
                                responseSearchShare(jsonObject);
                                Log.d(MyApplication.getContext().getString(R.string.domain), responseStr);
                            } catch (Exception e){
                                jsonObject = new JSONObject();
                                jsonObject.put("error", "后台返回异常信息");
                                Log.e(MyApplication.getContext().getString(R.string.domain), e.getMessage());
                                responseSearchShare(jsonObject);
                            }
                        }
                    });
                }else{
                    Toast.makeText(FileActivity.this, "请输入合法分享链接", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }


    /**
     * 将搜索分享链接的结果响应到UI中，切换到主线程
     * @param jsonObject
     */
    private void responseSearchShare(final JSONObject jsonObject){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(jsonObject.containsKey("error")){
                    Toast.makeText(FileActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                }else{
                    String responseTxt = jsonObject.getString("response");
                    final FileLink shareFileLink = JSON.parseObject(responseTxt, FileLink.class);

                    List<FileLink> shareFileList = new ArrayList<>();
                    shareFileList.add(shareFileLink);
                    FileLinkAdapter shareFileAdapter = new FileLinkAdapter(FileActivity.this, shareFileList, FileActivity.this);

                    // Toast.makeText(FileActivity.this, fileLink.toString(), Toast.LENGTH_SHORT).show();
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(FileActivity.this);
                    View viewGetShare = getLayoutInflater().inflate(R.layout.view_dialog_get_share, null, false);
                    final ListView shareListView = (ListView) viewGetShare.findViewById(R.id.list_view);

                    shareListView.setAdapter(shareFileAdapter);

                    dialog.setView(viewGetShare);
                    dialog.setCancelable(true);
                    final AlertDialog alert = dialog.create();
                    // 取消按钮
                    viewGetShare.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                        }
                    });
                    // 获取分享按钮
                    viewGetShare.findViewById(R.id.get_share).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // getShare()
                            // Log.d(MyApplication.getContext().getString(R.string.domain), shareFileLink.toString());
                            getShare(shareFileLink.getLinkId());
                            alert.dismiss();
                        }
                    });
                    alert.show();
                    // 设置窗体大小
                    // alert.getWindow().setLayout(900, 600);


                }

            }
        });
    }


    /**
     * 获取分享链接后保存到当前文件夹下
     * @param linkId
     */
    private void getShare(int linkId){
        String address = this.getString(R.string.domain) + "FileShare";
        RequestBody requestBody = new FormBody.Builder()
                .add("method", "getShare")
                .add("linkId", Integer.toString(linkId))
                .add("userId", Integer.toString(selfUser.getUserId()))
                .add("parent", Integer.toString(fileLink.getLinkId()))
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("error", e.getMessage());
                responseGetShare(jsonObject);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                JSONObject jsonObject;
                try{
                    String responseStr = response.body().string().trim();
                    jsonObject = JSON.parseObject(responseStr);
                    responseGetShare(jsonObject);
                } catch (Exception e){
                    jsonObject = new JSONObject();
                    jsonObject.put("error", "后台返回异常信息");
                    responseGetShare(jsonObject);
                }
            }
        });
    }


    /**
     * 获取分享链接后响应到UI界面，切换到主线程
     * @param jsonObject
     */
    private void responseGetShare(final JSONObject jsonObject){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(jsonObject.containsKey("error")){
                    Toast.makeText(FileActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                } else if(jsonObject.containsKey("response")){
                    swipeRefresh.setRefreshing(true);
                    getFileLinks(fileLink.getLinkId());
                }
            }
        });
    }




    /**
     * ListView 点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileLink childFileLink = fileLinkList.get(position);
        if(childFileLink.getIsFolder() == 'y') {        // 文件夹
            Bundle bundle = new Bundle();
            bundle.putSerializable("fileLink", childFileLink);
            Intent intent = new Intent(FileActivity.this, FileActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            // Log.d("FileActivity.java", fileLink.toString());
        }
    }


    /**
     * 响应FileLinkAdapter中自定义接口，响应ListView内部控件点击事件
     * @param v
     */
    @Override
    public void click(View v) {
        switch (v.getId()) {
            case R.id.layout_file_check:
                if(fileCheckView != null){
                    fileCheckView.setImageResource(R.drawable.file_uncheck);
                }
                fileCheckView = (ImageView) v.findViewById(R.id.file_check);
                fileCheckView.setImageResource(R.drawable.file_check);
                checkPosition = (Integer) v.getTag();
                if(bnavFileMenu.getVisibility() == View.GONE) {
                    // Toast.makeText(FileActivity.this, "hello: " + (Integer) v.getTag(), Toast.LENGTH_SHORT).show();
                    bnavFileMenu.setVisibility(View.VISIBLE);
                    isBnavFileMenu = true;
                }
                break;
            default:
        }
    }

    /**
     * ListView 长按事件
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        FileLink childFileLink = fileLinkList.get(position);
        listView.setItemChecked(position, true);

        if(fileCheckView != null){
            fileCheckView.setImageResource(R.drawable.file_uncheck);
        }
        fileCheckView = (ImageView) view.findViewById(R.id.file_check);
        fileCheckView.setImageResource(R.drawable.file_check);
        checkPosition = position;
        if(bnavFileMenu.getVisibility() == View.GONE) {
            bnavFileMenu.setVisibility(View.VISIBLE);
            isBnavFileMenu = true;
        }
        // Toast.makeText(FileActivity.this, childFileLink.toString(), Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * 加载菜单资源文件
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_file, menu);
        return true;
    }


    /**
     * 顶部导航栏监听器
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.file_more:
                // Toast.makeText(FileActivity.this, "上传文件", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putSerializable("fileLink", fileLink);
                Intent uploadIntent = new Intent(FileActivity.this, FileUploadActivity.class);
                uploadIntent.putExtras(bundle);
                startActivityForResult(uploadIntent, RESULT_UPLOAD);
                break;
            case R.id.file_download:
                // Toast.makeText(FileActivity.this, "下载文件", Toast.LENGTH_SHORT).show();
                Intent downloadIntent = new Intent(FileActivity.this, FileDownloadActivity.class);
                startActivity(downloadIntent);
                break;
            case R.id.user_share:
                Intent userShareIntent = new Intent(FileActivity.this, FileUserShareActivity.class);
                startActivity(userShareIntent);
                break;
            case R.id.file_sort:
                Toast.makeText(FileActivity.this, "这里以后添加扩展功能", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "别点我，呜呜", Toast.LENGTH_SHORT).show();
        }
        return true;
    }




    /**
     * 活动返回结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RESULT_UPLOAD:
                if(resultCode == RESULT_OK){
                    getFileLinks(fileLink.getLinkId());
                }
                break;
            default:
        }
    }


    /**
     * 底部导航栏item选择监听器 + 滑动菜单监听器
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_call:
                // Toast.makeText(FileActivity.this, "你点击了Call", Toast.LENGTH_SHORT).show();
                callPhone(selfUser.getPhoneNum());
                break;
            case R.id.nav_friends:
                // Toast.makeText(FileActivity.this, "你点击了Friends", Toast.LENGTH_SHORT).show();
                Intent friendsIntent2 = new Intent(FileActivity.this, FriendsActivity.class);
                startActivity(friendsIntent2);
                break;
            case R.id.nav_location:
                // Toast.makeText(FileActivity.this, "你点击了Location: " , Toast.LENGTH_SHORT).show();
                Intent locationIntent = new Intent(FileActivity.this, LocationActivity.class);
                startActivity(locationIntent);
                break;
            case R.id.nav_mail:
                // Toast.makeText(FileActivity.this, "你点击了Mail", Toast.LENGTH_SHORT).show();
                Toast.makeText(FileActivity.this, "功能扩展中...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_task:
                // Toast.makeText(FileActivity.this, "你点击了Task", Toast.LENGTH_SHORT).show();
                Toast.makeText(FileActivity.this, "功能扩展中...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                SharedPreferenceUtil.deleteLoginUser(FileActivity.this);
                SharedPreferenceUtil.deleteRootFolder();
                Intent loginIntent = new Intent(FileActivity.this, login.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginIntent);
                break;
            case R.id.bnav_file:
                // Toast.makeText(FileActivity.this, "你已经在文件页面了", Toast.LENGTH_SHORT).show();
                fileLink = SharedPreferenceUtil.getRootFolder();    // 回到根文件夹
                getFileLinks(fileLink.getLinkId());
                break;
            case R.id.bnav_message:
                Intent messageIntent = new Intent(FileActivity.this, MessageActivity.class);
                startActivity(messageIntent);
                break;
            case R.id.bnav_friends:
                Intent friendsIntent = new Intent(FileActivity.this, FriendsActivity.class);
                startActivity(friendsIntent);
                break;
            case R.id.bnav_trends:
                Toast.makeText(FileActivity.this, "即将跳转到动态页面", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bnav_file_download:
                // Toast.makeText(FileActivity.this, "下载", Toast.LENGTH_SHORT).show();
                if (fileLinkList.get(checkPosition).getIsFolder() == 'y') {
                    Toast.makeText(FileActivity.this, "文件夹下载功能开发中...", Toast.LENGTH_SHORT).show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("fileLink", fileLinkList.get(checkPosition));
                    Intent downloadIntent = new Intent(FileActivity.this, FileDownloadActivity.class);
                    downloadIntent.putExtras(bundle);
                    startActivity(downloadIntent);
                }
                break;
            case R.id.bnav_file_share:
                // Toast.makeText(FileActivity.this, "分享", Toast.LENGTH_SHORT).show();
                createFileShare(fileLinkList.get(checkPosition).getLinkId());
                break;
            case R.id.bnav_file_delete:
                Toast.makeText(FileActivity.this, "删除", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bnav_file_rename:
                // Toast.makeText(FileActivity.this, "重命名", Toast.LENGTH_SHORT).show();
                final AlertDialog.Builder dialog = new AlertDialog.Builder(FileActivity.this);
                View viewRename = getLayoutInflater().inflate(R.layout.view_dialog_rename, null, false);
                final EditText inputNewName = (EditText) viewRename.findViewById(R.id.input_new_name);
                dialog.setView(viewRename);
                dialog.setCancelable(true);
                final AlertDialog alert = dialog.create();
                // 取消按钮
                viewRename.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });
                // 重命名按钮
                viewRename.findViewById(R.id.rename).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newName = inputNewName.getText().toString().trim();
                        if (!"".equals(newName)) {
                            setFileNewName(fileLinkList.get(checkPosition).getLinkId(), newName);
                            alert.dismiss();
                        }
                    }
                });
                alert.show();
                // 设置窗体大小
                alert.getWindow().setLayout(900, 600);
                break;
            case R.id.bnav_file_more:
                Toast.makeText(FileActivity.this, "更多", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "来，快来点我", Toast.LENGTH_SHORT).show();
        }

        return true;
    }


    private void callPhone(String phoneNum){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }


    private void setFileNewName(int linkId, String newName){
        String url = this.getString(R.string.domain) + "Folders";
        RequestBody requestBody = new FormBody.Builder()
                .add("method", "rename")
                .add("linkId", Integer.toString(linkId))
                .add("newName", newName)
                .build();
        HttpUtil.sendOkHttpRequest(url, requestBody, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("error", e.getMessage());
                showRenameResponse(jsonObject.toJSONString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseStr = response.body().string().trim();
                showRenameResponse(responseStr);
            }
        });
    }

    private void showRenameResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject responseJson = JSON.parseObject(response);
                if(responseJson.containsKey("error")){
                    Toast.makeText(FileActivity.this, response, Toast.LENGTH_SHORT).show();
                }else if(responseJson.containsKey("response")){
                    swipeRefresh.setRefreshing(true);
                    getFileLinks(fileLink.getLinkId());
                }
            }
        });
    }

    private void createFileShare(int linkId){
        String url = this.getString(R.string.domain) + "FileShare";
        RequestBody requestBody = new FormBody.Builder()
                .add("method", "create")
                .add("linkId", Integer.toString(linkId))
                .build();
        HttpUtil.sendOkHttpRequest(url, requestBody, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("FileActivity.java", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseStr = response.body().string();
                JSONObject jsonObject = JSON.parseObject(responseStr);
                if(jsonObject.containsKey("error")){
                    Log.e("FileActivity.java", responseStr);
                }else{
                    Intent userShareIntent = new Intent(FileActivity.this, FileUserShareActivity.class);
                    startActivity(userShareIntent);
                }
            }
        });
    }



    /**
     * 下拉刷新监听器
     */
    @Override
    public void onRefresh() {
        // Toast.makeText(FileActivity.this, "别拉我，我自己t", Toast.LENGTH_SHORT).show();
        getFileLinks(fileLink.getLinkId());
    }


    /**
     * 获取当前目录下某一文件夹的文件列表
     */
    private void getFileLinks(int linkId){
        String address = this.getString(R.string.domain) + "FileSearch";
        RequestBody requestBody = new FormBody.Builder()
                .add("method", "searchFolder")
                .add("linkId", Integer.toString(linkId))
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("FileActivity", e.getMessage());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("error", e.getMessage());
                showResponse(jsonObject.toJSONString());
                if(swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseStr = response.body().string().trim();
                showResponse(responseStr);
                if(swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
            }
        });
    }



    /**
     * 获取根文件夹链接
     * 转移到登录时调用
     */
    /* private void getRootFolder(int userId){
        String address = this.getString(R.string.domain) + "FileSearch";
        RequestBody requestBody = new FormBody.Builder()
                .add("method", "getRootFolder")
                .add("userId", Integer.toString(userId))
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                showResponse(e.getMessage());
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                showResponse(response.body().string());
                swipeRefresh.setRefreshing(false);
            }
        });
    }*/




    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject responseJson = JSON.parseObject(response);
                // Toast.makeText(FileActivity.this, responseJson.toJSONString(), Toast.LENGTH_SHORT).show();
                if(responseJson.getString("error") != null){        // 异常信息
                    Toast.makeText(FileActivity.this, responseJson.getString("error"), Toast.LENGTH_SHORT).show();
                } else{      // 正确信息
                    JSONArray folderJson = responseJson.getJSONArray("folderList");
                    JSONArray fileJson = responseJson.getJSONArray("fileList");
                    fileLinkList.clear();
                    if(folderJson != null){
                        List<FileLink> folderList = JSONObject.parseArray(folderJson.toJSONString(), FileLink.class);
                        fileLinkList.addAll(folderList);
                    }
                    if(fileJson != null){
                        List<FileLink> fileList = JSONObject.parseArray(fileJson.toJSONString(), FileLink.class);
                        fileLinkList.addAll(fileList);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }



    /**
     * 修改HomeAsUp图标
     * @param actionBar
     */
    private void changeHomeAsUp(ActionBar actionBar){
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }


    /**
     * 返回事件
     */
    @Override
    public void onBackPressed() {
        if(isBnavFileMenu){     // 退出选中文件模式
            fileCheckView.setImageResource(R.drawable.file_uncheck);
            bnavFileMenu.setVisibility(View.GONE);
            isBnavFileMenu = false;
        }else {
            super.onBackPressed();
        }
    }


}
