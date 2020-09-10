package cn.qixqi.pan;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import com.alibaba.fastjson.JSON;


import org.jetbrains.annotations.NotNull;

import cn.qixqi.pan.adapter.UserAdapter;
import cn.qixqi.pan.bean.User;
import cn.qixqi.pan.util.HttpUtil;
import cn.qixqi.pan.util.SharedPreferenceUtil;
import okhttp3.Call;
import okhttp3.Response;


/**
 * todo 底部导航栏布局有问题，与ListView中最后一项重叠，并且Search<Activity>中导航栏长度不够啊
 */
public class FriendsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {


    private User selfUser;          // 当前登录用户
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SwipeRefreshLayout swipeRefresh;
    private ListView listView;
    private List<User> friend_list = new ArrayList<>();
    private UserAdapter adapter = null;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // 获取当前登录用户
        selfUser = SharedPreferenceUtil.getLoginUser(FriendsActivity.this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bnav_menu);
        bottomNavigationView.setSelectedItemId(R.id.bnav_friends);
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

        initFriends();
        listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        adapter = new UserAdapter(FriendsActivity.this, R.layout.user_item, friend_list);
        listView.setAdapter(adapter);
    }

    private void initFriends(){
        /* String time = df.format(new Date());
        for(int i=0; i<3; i++){
            User user1 = new User(100000, "yyk", 'm', "110", time, time, time);
            friend_list.add(user1);
            User user2 = new User(100001, "cy", 'f', "120", time, time, time);
            friend_list.add(user2);
            User user3 = new User(100002, "zxh", 'f', "121", time, time, time);
            friend_list.add(user3);
            User user4 = new User(100003, "xz", 'f', "122", time, time, time);
            friend_list.add(user4);
            User user5 = new User(100004, "zyx", 'f', "123", time, time, time);
            friend_list.add(user5);
        }*/
        // refreshFriends();
        swipeRefresh.setRefreshing(true);
        refreshFriends();
    }


    /**
     * ListView 各项点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User user = friend_list.get(position);
        // 实例化一个Bundle对象
        Bundle bundle = new Bundle();
        bundle.putSerializable("friend", user);
        Intent friendHomeIntent = new Intent(FriendsActivity.this, FriendHomeActivity.class);
        friendHomeIntent.putExtras(bundle);
        startActivity(friendHomeIntent);
        // Toast.makeText(FriendsActivity.this, user.toString(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 加载菜单资源文件
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_friends, menu);
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
                Intent searchIntent = new Intent(FriendsActivity.this, Search.class);
                startActivity(searchIntent);
                // Toast.makeText(this, "即将跳转到搜索页面", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_friends_more:
                Toast.makeText(this, "更多基友", Toast.LENGTH_SHORT).show();
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
                Intent fileIntent = new Intent(FriendsActivity.this, FileActivity.class);
                startActivity(fileIntent);
                break;
            case R.id.bnav_message:
                Intent messageIntent = new Intent(FriendsActivity.this, MessageActivity.class);
                startActivity(messageIntent);
                break;
            case R.id.bnav_friends:
                Toast.makeText(this, "您已经在联系人框了", Toast.LENGTH_SHORT).show();
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
                drawerLayout.closeDrawer(GravityCompat.START);
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
        refreshFriends();
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



    private void refreshFriends(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtil.sendOkHttpRequest("https://ourvultr.club:8443/qq/Friends?method=searchAll&userId1=" + selfUser.getUserId(), new okhttp3.Callback(){
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        showResponse("error" + e.getMessage(), false);
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
                // swipeRefresh.setRefreshing(false);
            }
        }).start();
    }


    /**
     * 切换为主线程修改UI
     * @param response
     */
    private void showResponse(final String response, final boolean success){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(success){
                    friend_list.clear();
                    // OkHttp 获取的数据自带换行
                    if("empty\n".equals(response)){
                        Toast.makeText(FriendsActivity.this, "亲，你还没有好友哦！", Toast.LENGTH_SHORT).show();
                    }else {
                        // 第一种方式
                        /* friend_list.clear();
                        friend_list = JSON.parseArray(response, User.class);
                        adapter = new UserAdapter(FriendsActivity.this, R.layout.user_item, friend_list);
                        listView.setAdapter(adapter); */

                        // 第二种方式
                        List<User> list = JSON.parseArray(response, User.class);
                        for (User user : list) {
                            friend_list.add(user);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(FriendsActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
