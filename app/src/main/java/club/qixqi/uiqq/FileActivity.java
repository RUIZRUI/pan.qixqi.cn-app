package club.qixqi.uiqq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import club.qixqi.uiqq.adapter.FileLinkAdapter;
import club.qixqi.uiqq.bean.FileLink;
import club.qixqi.uiqq.bean.User;
import club.qixqi.uiqq.util.HttpUtil;
import club.qixqi.uiqq.util.SharedPreferenceUtil;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
    NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener{

    private User selfUser;
    private FileLink fileLink;             // 当前目录
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SwipeRefreshLayout swipeRefresh;
    private ListView listView;
    private List<FileLink> fileLinkList = new ArrayList<>();
    private FileLinkAdapter adapter = null;

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        // 获取当前用户
        selfUser = SharedPreferenceUtil.getLoginUser(FileActivity.this);
        setFileLink();
        // Toast.makeText(this, fileLink.toString(), Toast.LENGTH_SHORT).show();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bnav_menu);
        bottomNavigationView.setSelectedItemId(R.id.bnav_file);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

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
        adapter = new FileLinkAdapter(FileActivity.this, R.layout.filelink_item, fileLinkList);
        listView.setAdapter(adapter);
    }


    private void initFileLink(){
        String time = df.format(new Date());
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
        }
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
                startActivity(uploadIntent);
                break;
            case R.id.file_download:
                // Toast.makeText(FileActivity.this, "下载文件", Toast.LENGTH_SHORT).show();
                Intent downloadIntent = new Intent(FileActivity.this, FileDownloadActivity.class);
                startActivity(downloadIntent);
                break;
            case R.id.file_sort:
                Toast.makeText(FileActivity.this, "选择排列方式", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "别点我，呜呜", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    /**
     * 底部导航栏item选择监听器 + 滑动菜单监听器
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_call:
                Toast.makeText(FileActivity.this, "你点击了Call", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_friends:
                Toast.makeText(FileActivity.this, "你点击了Friends", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_location:
                Toast.makeText(FileActivity.this, "你点击了Location", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_mail:
                Toast.makeText(FileActivity.this, "你点击了Mail", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_task:
                Toast.makeText(FileActivity.this, "你点击了Task", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bnav_file:
                Toast.makeText(FileActivity.this, "你已经在文件页面了", Toast.LENGTH_SHORT).show();
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
            default:
                Toast.makeText(this, "来，快来点我", Toast.LENGTH_SHORT).show();
        }

        return true;
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
        String address = "https://www.ourvultr.club:8443/qq/FileSearch";
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
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseStr = response.body().string().trim();
                showResponse(responseStr);
                swipeRefresh.setRefreshing(false);
            }
        });
    }



    /**
     * 获取根文件夹链接
     * 转移到登录时调用
     */
    /* private void getRootFolder(int userId){
        String address = "https://www.ourvultr.club:8443/qq/FileSearch";
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
                }else{      // 正确信息
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

}
