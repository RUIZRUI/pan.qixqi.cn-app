package cn.qixqi.pan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import cn.qixqi.pan.util.HttpUtil;
import cn.qixqi.pan.util.SharedPreferenceUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import cn.qixqi.pan.bean.User;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * todo
 * 1. 添加好友后自动创建会话，应该在后台自动完成，不应该再次发送请求
 */


public class AddFriendActivity extends AppCompatActivity implements View.OnClickListener{

    private User selfUser;          // 当前登录用户
    private User user;          // 搜索出的陌生人
    private TextView userId;
    private TextView username;
    private CircleImageView userIcon;
    private TextView userRegisterTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        // 获取当前登录用户
        selfUser = SharedPreferenceUtil.getLoginUser(AddFriendActivity.this);

        // 获取 user
        getUser();
        // Toast.makeText(this, user.toString(), Toast.LENGTH_SHORT).show();

        // 使用获取的user更新视图
        userId = (TextView) findViewById(R.id.user_id);
        username = (TextView) findViewById(R.id.user_name);
        userIcon = (CircleImageView) findViewById(R.id.user_icon);
        userRegisterTime = (TextView) findViewById(R.id.user_register_time);
        initViewWithUser();

        // 页面点击时间监听
        userIcon.setOnClickListener(this);
        TextView addFriend = (TextView) findViewById(R.id.add_friend);
        addFriend.setOnClickListener(this);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        changeHomeAsUp(actionBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_icon:
                Toast.makeText(this, user.getIcon(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_friend:
                // Toast.makeText(this, "即将添加好友", Toast.LENGTH_SHORT).show();
                addFriend(selfUser.getUserId(), user.getUserId());
                Toast.makeText(this, "添加好友成功，即将跳转", Toast.LENGTH_SHORT).show();
                addSession(selfUser.getUserId(), selfUser.getUsername(), user.getUserId());
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
            default:
        }
        return true;
    }

    /**
     * 从上一个活动获取user
     */
    private void getUser(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user = (User) bundle.getSerializable("user");
    }


    private void initViewWithUser(){
        userId.setText("账号: " + user.getUserId());
        username.setText(user.getUsername());
        userRegisterTime.setText("注册时间: " + user.getRegisterTime());
    }


    /**
     * todo
     * 1. 好友验证
     * @param userId1
     * @param userId2
     */
    // 访问服务器，添加好友
    private void addFriend(int userId1, int userId2){
        String address = "https://www.ourvultr.club:8443/qq/Friends";
        RequestBody requestBody = new FormBody.Builder()
                .add("method", "add")
                .add("userId1", Integer.toString(userId1))
                .add("userId2", Integer.toString(userId2))
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new okhttp3.Callback(){
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
                Toast.makeText(AddFriendActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 好友添加成功后创建会话
     * @param userId1       发送人id
     * @param username1     最后一条消息发送者名称
     * @param userId2       接受人id
     */
    private void addSession(int userId1, String username1, int userId2){
        String address = "https://www.ourvultr.club:8443/qq/SessionServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("method", "create")
                .add("userId1", Integer.toString(userId1))
                .add("username1", username1)
                .add("userId2", Integer.toString(userId2))
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new okhttp3.Callback(){
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
