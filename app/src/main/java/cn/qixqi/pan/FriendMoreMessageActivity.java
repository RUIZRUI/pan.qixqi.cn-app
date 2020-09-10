package cn.qixqi.pan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import cn.qixqi.pan.bean.User;

/**
 * todo
 * 1. 删除布局文件中的默认信息
 * 2. 布局是否更改为ListView更好
 */


public class FriendMoreMessageActivity extends AppCompatActivity {

    private User friend;
    private TextView friendName;
    private TextView friendId;
    private TextView friendSex;
    private TextView friendPhoneNum;
    private TextView friendRegisterTime;
    private TextView friendLastLoginTime;
    private TextView friendRelationTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_more_message);

        // 获取friend
        getFriend();

        friendName = (TextView) findViewById(R.id.friend_name);
        friendId = (TextView) findViewById(R.id.friend_id);
        friendSex = (TextView) findViewById(R.id.friend_sex);
        friendPhoneNum =(TextView) findViewById(R.id.friend_phone_num);
        friendRegisterTime = (TextView) findViewById(R.id.friend_register_time);
        friendLastLoginTime = (TextView) findViewById(R.id.friend_last_login_time);
        friendRelationTime = (TextView) findViewById(R.id.friend_relation_time);
        initViewWithFriend();

        // 顶部导航栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        changeHomeAsUp(actionBar);
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
     * 从上个Activity中获取friend
     */
    private void getFriend(){
        // 获取启动FriendUserActivity的Intent
        Intent intent = getIntent();
        // 实例化一个Bundle对象
        Bundle bundle = intent.getExtras();
        // 获取bundle中的User对象
        friend = (User) bundle.getSerializable("friend");
    }

    /**
     * 读入friend内信息
     */
    private void initViewWithFriend(){
        friendName.setText("用户名：" + friend.getUsername());
        friendId.setText("账号：" + friend.getUserId());
        friendSex.setText("性别：" + friend.getSex());
        friendPhoneNum.setText("手机号：" + friend.getPhoneNum());
        friendRegisterTime.setText("注册时间：" + friend.getRegisterTime());
        friendLastLoginTime.setText("最近登录：" + friend.getLastLoginTime());
        friendRelationTime.setText("成为好友时间：" + friend.getRelationTime());
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
