package club.qixqi.uiqq;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import club.qixqi.uiqq.adapter.SessionAdapter;
import club.qixqi.uiqq.bean.Sessions;
import club.qixqi.uiqq.bean.User;
import club.qixqi.uiqq.util.HttpUtil;
import club.qixqi.uiqq.util.SharedPreferenceUtil;
import okhttp3.Call;
import okhttp3.Response;

public class MessageActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, ListView.OnItemClickListener {

    private User selfUser;          // 当前登录用户
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SwipeRefreshLayout swipeRefresh;
    private ListView listView;
    private List<Sessions> sessionsList = new ArrayList<>();
    private SessionAdapter adapter = null;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // 获取当前登录用户
        selfUser = SharedPreferenceUtil.getLoginUser(MessageActivity.this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bnav_menu);
        bottomNavigationView.setSelectedItemId(R.id.bnav_message);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBar actionBar = getSupportActionBar();
        changeHomeAsUp(actionBar);      // 修改HomeAsUp图标

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_call);       // 默认选中
        navigationView.setNavigationItemSelectedListener(this);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(this);

        initSessions();
        listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        adapter = new SessionAdapter(MessageActivity.this, R.layout.session_item, sessionsList);
        listView.setAdapter(adapter);

    }


    private void initSessions(){
        /* for(int i=0; i<10; i++){
            Sessions sessions = new Sessions(1,1,1,1, "cy：好久不见", "cy", "下午5:30", 'w', 'r');
            sessionsList.add(sessions);
        }*/
        swipeRefresh.setRefreshing(true);
        refreshSessions();
    }


    /**
     * ListView item 点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Sessions sessions = sessionsList.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("sessions", sessions);
        Intent sessionIntent = new Intent(MessageActivity.this, SessionActivity.class);
        sessionIntent.putExtras(bundle);
        startActivityForResult(sessionIntent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                // Toast.makeText(MessageActivity.this, "hello, Message", Toast.LENGTH_SHORT).show();
                initSessions();
                break;
            default:
        }
    }

    /**
     * 加载菜单资源文件
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar, menu);
        return true;
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
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.nav_search:
                Intent searchIntent = new Intent(MessageActivity.this, Search.class);
                startActivity(searchIntent);
                // Toast.makeText(this, "即将跳转到搜索页面", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_camera:
                Toast.makeText(this, "即将打开摄像头", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_more:
                Toast.makeText(this, "即将打开更多功能", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }


    /**
     * 底部导航栏菜单选择监听器 + 滑动菜单选择监听器
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.bnav_file:
                Intent fileIntent = new Intent(MessageActivity.this, FileActivity.class);
                startActivity(fileIntent);
                break;
            case R.id.bnav_message:
                Toast.makeText(this, "您已经在消息框了", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bnav_friends:
                // Toast.makeText(this, "您选择了联系人框", Toast.LENGTH_SHORT).show();
                Intent friendIntent = new Intent(MessageActivity.this, FriendsActivity.class);
                startActivity(friendIntent);
                break;
            case R.id.bnav_trends:
                Toast.makeText(this, "您选择了动态框", Toast.LENGTH_SHORT).show();
                break;

                // 滑动菜单选择监听器
            case R.id.nav_call:
                Toast.makeText(this, "您想打电话吗？", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_friends:
                // Toast.makeText(this, "您想找朋友聊天吗？", Toast.LENGTH_SHORT).show();
                Intent friendsIntent2 = new Intent(MessageActivity.this, FriendsActivity.class);
                startActivity(friendsIntent2);
                break;
            case R.id.nav_location:
                Toast.makeText(this, "您想尝试位置服务吗？", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_mail:
                Toast.makeText(this, "您想查看邮箱吗？", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_task:
                Toast.makeText(this, "您想做任务吗？", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

    /**
     * 下拉刷新监听器
     */
    @Override
    public void onRefresh(){
        refreshSessions();
    }


    private void refreshSessions(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String address = "https://www.ourvultr.club:8443/qq/SessionServlet?method=searchAll&userId=" + selfUser.getUserId();
                HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback(){
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        showResponse(e.getMessage(), false);
                        if(swipeRefresh.isRefreshing()) {
                            swipeRefresh.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        showResponse(response.body().string(), true);
                        if(swipeRefresh.isRefreshing()) {
                            swipeRefresh.setRefreshing(false);
                        }
                    }
                });
            }
        }).start();
    }


    private void showResponse(final String reponse, final boolean success){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(success){
                    sessionsList.clear();
                    if("empty\n".equals(reponse)){
                        Toast.makeText(MessageActivity.this, "亲，你还没有会话哦！赶快添加好友创建吧！", Toast.LENGTH_SHORT).show();
                    }else{
                        List<Sessions> list = JSON.parseArray(reponse, Sessions.class);
                        for(Sessions sessions : list){
                            sessionsList.add(sessions);
                            Log.d("qixqi.club", sessions.toString());
                        }
                        // Toast.makeText(MessageActivity.this, reponse, Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(MessageActivity.this, reponse, Toast.LENGTH_SHORT).show();
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
