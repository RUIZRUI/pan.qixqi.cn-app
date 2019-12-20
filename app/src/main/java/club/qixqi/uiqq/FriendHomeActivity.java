package club.qixqi.uiqq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import club.qixqi.uiqq.bean.User;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * todo
 * 1. 性别未知的图标尚未找到
 * 2. 布局文件中默认信息需要删除
 * 3. Linearlayout 添加点击效果
 */


public class FriendHomeActivity extends AppCompatActivity implements View.OnClickListener{

    private User friend;
    private TextView friendName;
    private ImageView friendSex;
    private TextView friendId;
    private TextView friendTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_home);

        // 获取friend
        getFriend();

        // 使用获取的friend更新视图
        friendName = (TextView) findViewById(R.id.friend_name);
        friendSex = (ImageView) findViewById(R.id.friend_sex);
        friendId = (TextView) findViewById(R.id.friend_id);
        friendTime = (TextView) findViewById(R.id.friend_time);
        initViewWithFriend();

        // 页面点击事件
        CircleImageView friendIcon = (CircleImageView) findViewById(R.id.friend_icon);
        LinearLayout friendTrends = (LinearLayout) findViewById(R.id.friend_trends);
        LinearLayout friendMoreMessage = (LinearLayout) findViewById(R.id.friend_more_message);
        LinearLayout sendMessage = (LinearLayout) findViewById(R.id.send_message);
        friendIcon.setOnClickListener(this);
        friendTrends.setOnClickListener(this);
        friendMoreMessage.setOnClickListener(this);
        sendMessage.setOnClickListener(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        changeHomeAsUp(actionBar);
    }

    /**
     * 加载菜单资源文件
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_friend_home, menu);
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
                finish();
                break;
            case R.id.nav_friend_setting:
                Toast.makeText(this, "即将弹出好友设置", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.friend_icon:
                Toast.makeText(this, "你点我头像干啥", Toast.LENGTH_SHORT).show();
                break;
            case R.id.friend_trends:
                Toast.makeText(this, "好友动态", Toast.LENGTH_SHORT).show();
                break;
            case R.id.friend_more_message:
                // Toast.makeText(this, "好友详细信息", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putSerializable("friend", friend);
                Intent friendMoreMessage = new Intent(FriendHomeActivity.this, FriendMoreMessageActivity.class);
                friendMoreMessage.putExtras(bundle);
                startActivity(friendMoreMessage);
                break;
            case R.id.send_message:
                Toast.makeText(this, "发消息", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
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


    private void initViewWithFriend(){
        // 用户名
        friendName.setText(friend.getUsername());
        // 用户性别
        if(friend.getSex() == 'm'){
            friendSex.setImageResource(R.drawable.male);
        }else if(friend.getSex() == 'f'){
            friendSex.setImageResource(R.drawable.female);
        }else{      // 未知

        }
        friendId.setText("账号: " + friend.getUserId());
        friendTime.setText("注册时间: " + friend.getRegisterTime());
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
