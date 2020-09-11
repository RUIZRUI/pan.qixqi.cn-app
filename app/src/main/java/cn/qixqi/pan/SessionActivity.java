package cn.qixqi.pan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.text.SimpleDateFormat;
import java.util.TimerTask;

import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import cn.qixqi.pan.adapter.MessageAdapter;
import cn.qixqi.pan.bean.Message;
import cn.qixqi.pan.bean.Sessions;
import cn.qixqi.pan.bean.User;
import cn.qixqi.pan.context.MyApplication;
import cn.qixqi.pan.util.HttpUtil;
import cn.qixqi.pan.util.SessionsUtil;
import cn.qixqi.pan.util.SharedPreferenceUtil;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * todo
 * 1. 系统消息应该更靠近下一条数据
 * 2. 接收消息的框左内边距应该更大些
 * 3. ListView 每一项的点击效果应该去除(OK)
 * 4. 输入框满的时候可以向上扩展
 */


public class SessionActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    // showResponse的参数来源
    private static final int DEFAULT = 0;
    private static final int SEARCHALL = 1;
    private static final int ADDRESPONSE = 2;
    private static final int ADDPUSH = 3;
    private WebSocket mSocket;

    private Sessions sessions = null;
    private User selfUser;

    private ImageView sessionAudio;
    private ImageView sessionPicture;
    private ImageView sessionCamera;
    private ImageView sessionFace;
    private ImageView sessionMoreOpt;
    private EditText inputMessage;
    private SwipeRefreshLayout swipeRefresh;
    private ListView listView;
    private MessageAdapter adapter = null;
    private List<Message> messageList = new ArrayList<>();
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        // 获取当前登录用户
        selfUser = SharedPreferenceUtil.getLoginUser(SessionActivity.this);

        // 获取会话
        getSessions();


        inputMessage = (EditText) findViewById(R.id.input_message);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(this);
        Button sendMessage = (Button) findViewById(R.id.send_message);
        sessionAudio = (ImageView) findViewById(R.id.session_audio);
        sessionPicture = (ImageView) findViewById(R.id.session_picture);
        sessionCamera = (ImageView) findViewById(R.id.session_camera);
        sessionFace = (ImageView) findViewById(R.id.session_face);
        sessionMoreOpt = (ImageView) findViewById(R.id.session_more_opt);
        sendMessage.setOnClickListener(this);
        sessionAudio.setOnClickListener(this);
        sessionPicture.setOnClickListener(this);
        sessionCamera.setOnClickListener(this);
        sessionFace.setOnClickListener(this);
        sessionMoreOpt.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        changeHomeAsUp(actionBar);

        // 消息列表
        // initMessage();
        adapter = new MessageAdapter(SessionActivity.this, messageList, selfUser);
        // Log.e("SessionActivity.java", adapter.toString());
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        // 建立 WebSocket 连接
        String address = this.getString(R.string.websocketDomain) + "MessageSocket/" + selfUser.getUserId();
        EchoWebSocketListener socketListener = new EchoWebSocketListener();
        HttpUtil.sendOkHttpRequestWithWebSocket(address, socketListener);
    }


    /**
     * 初始化消息列表
     */
    private void initMessage(){
        /* String time = df.format(new Date());
        for(int i=0; i<5; i++) {
            Message message1 = new Message(801935, 801935, "zx", "default-icon.png", 207794, 'f', 'w', "嗯嗯嗯", time, 'i');
            messageList.add(message1);
            Message message2 = new Message(801935, 207794, "zxh", "default-icon.png", 801935, 'f', 'w', "嗯嗯", time, 'i');
            messageList.add(message2);
            Message message3 = new Message(801935, 801935, "zx", "default-icon.png", 207794, 'f', 'w', "真的吗？", time, 'i');
            messageList.add(message3);
            Message message4 = new Message(0, 0, "", "", 0, 'f', 's', "啦啦啦", time, 'i');
            messageList.add(message4);
            Message message5 = new Message(801935, 207794, "zxh", "default-icon.png", 801935, 'f', 'w', "是的呢！", time, 'i');
            messageList.add(message5);
            messageList.add(message4);
            Message message6 = new Message(801935, 207794, "zxh", "default-icon.png", 801935, 'f', 'w', "设置后，会改变聊天、菜单和朋友圈的字体大小。如果在使用过程中存在问题或意见，可反馈给微信团队", time, 'i');
            messageList.add(message6);
            Message message7 = new Message(801935, 801935, "zx", "default-icon.png", 207794, 'f', 'w', "设置后，会改变聊天、菜单和朋友圈的字体大小。如果在使用过程中存在问题或意见，可反馈给微信团队", time, 'i');
            messageList.add(message7);
        } */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("method", "searchAll");
        jsonObject.put("userId1", sessions.getUserId1());
        jsonObject.put("userId2", sessions.getUserId2());
        mSocket.send(jsonObject.toString());
    }


    private void getSessions(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        sessions = (Sessions) bundle.getSerializable("sessions");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_session, menu);
        return true;
    }


    /**
     * 下拉刷新监听事件
     */
    @Override
    public void onRefresh() {
        /* String address = MyApplication.getContext().getString(R.string.domain) + "Messages?method=searchAll&userId1=" + sessions.getUserId1() + "&userId2=" + sessions.getUserId2();
        HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                showResponse(e.getMessage(), SEARCHALL);
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                showResponse(response.body().string(), SEARCHALL);
                swipeRefresh.setRefreshing(false);
            }
        }); */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("method", "searchAll");
        jsonObject.put("userId1", sessions.getUserId1());
        jsonObject.put("userId2", sessions.getUserId2());
        mSocket.send(jsonObject.toString());
        // swipeRefresh.setRefreshing(false);
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
                Toast.makeText(this, "更多信息", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }


    /**
     * 点击事件处理
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_message:
                // Toast.makeText(this, "发送消息", Toast.LENGTH_SHORT).show();
                sendMessage();
                inputMessage.setText("");
                break;
            case R.id.session_audio:
                sessionAudio.setImageResource(R.drawable.bnav_audio_checked);
                break;
            case R.id.session_picture:
                sessionPicture.setImageResource(R.drawable.bnav_picture_checked);
                break;
            case R.id.session_camera:
                sessionCamera.setImageResource(R.drawable.bnav_camera_checked);
                break;
            case R.id.session_face:
                sessionFace.setImageResource(R.drawable.bnav_face_checked);
                break;
            case R.id.session_more_opt:
                sessionMoreOpt.setImageResource(R.drawable.bnav_more_checked);
                break;
            default:
        }
    }


    /**
     * 发送消息
     */
    private void sendMessage(){
        if(inputMessage.getText().length() == 0){
            return;
        }
        int theOtherId = SessionsUtil.getTheOtherId(selfUser.getUserId(), sessions);
        String msg = inputMessage.getText().toString();
        String sendTime = df.format(new Date());
        Message message = new Message(selfUser.getUserId(), selfUser.getUserId(), selfUser.getUsername(), selfUser.getIcon(), theOtherId, 'f', 'w', msg, sendTime, 'i');
        // Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show();
        Log.i("SessionActivity.java", message.toString());
        messageList.add(message);
        adapter.notifyDataSetChanged();
        postMessage(message);
    }

    private void postMessage(Message message){
        if(message == null){
            return;
        }
        /* String address = MyApplication.getContext().getString(R.string.domain) + "Messages";
        RequestBody requestBody = new FormBody.Builder()
                .add("method", "add")
                .add("message", JSON.toJSONString(message))
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                showResponse(e.getMessage(), ADD);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                showResponse(response.body().string(), ADD);
            }
        }); */
        JSON messageJson = (JSON) JSON.toJSON(message);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("method", "add");
        jsonObject.put("message", messageJson);
        Log.d("SessionActivity.java", jsonObject.toJSONString());
        mSocket.send(jsonObject.toString());
    }


    private void showResponse(final String response, final int source){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (source){
                    case SEARCHALL:
                        // Log.d(MyApplication.getContext().getString(R.string.domain), response);
                        if("error".equals(response) || "empty".equals(response)){       // 注意这里不是okhttp3响应数据，所以不能加\n
                            Toast.makeText(SessionActivity.this, "对方可能已经下线了", Toast.LENGTH_SHORT).show();
                        }else{
                            List<Message> list = JSON.parseArray(response, Message.class);
                            messageList.clear();
                            for(Message message : list){
                                messageList.add(message);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    case ADDRESPONSE:
                        Toast.makeText(SessionActivity.this, response, Toast.LENGTH_SHORT).show();
                        break;
                    case ADDPUSH:
                        Message addMessage = JSON.parseObject(response, Message.class);
                        messageList.add(addMessage);
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        Toast.makeText(SessionActivity.this, response, Toast.LENGTH_SHORT).show();
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
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }


    private final class EchoWebSocketListener extends WebSocketListener{

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosed(webSocket, code, reason);
            Log.i("SessionActivity.java", "closed reason: " + reason);
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosing(webSocket, code, reason);
            Log.i("SessionActivity.java", "closing reason: " + reason);
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            Log.e("SessionActivity.java", "failure :" + t.getMessage());
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);

            // 后台返回消息全是json格式
            JSONObject jsonObject = JSON.parseObject(text);
            if(jsonObject.containsKey("request")){
                JSONObject requestJson = jsonObject.getJSONObject("request");
                if("searchAll".equals(requestJson.getString("method"))){        // searchAll返回的响应内容
                    // todo: MessageActivity 与 SessionActivity联系起来
                    showResponse(jsonObject.getString("response"), SEARCHALL);
                    swipeRefresh.setRefreshing(false);
                }else if("addPush".equals(requestJson.getString("method"))){
                    showResponse(jsonObject.getString("push"), ADDPUSH);
                }else if("addResponse".equals(requestJson.getString("method"))){
                    showResponse(jsonObject.getString("response"), ADDRESPONSE);
                }

            }else{
                Log.i("SessionActivity.java", "接收消息：" + text);
                showResponse("接收消息：" + text, DEFAULT);
            }


            // 收到服务器端发送来的信息后，每隔25秒发送一次心跳包
            final String heart = "{\"method\":\"heart\", \"userId\": " + selfUser.getUserId() + "}";
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mSocket.send(heart);
                }
            }, 25000);
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            super.onMessage(webSocket, bytes);
            Log.i("SessionActivity.java", "接收字节：" + bytes.hex());
            showResponse("接收字节：" + bytes.hex(), DEFAULT);
        }

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);
            mSocket = webSocket;
            initMessage();
            Log.i("SessionActivity.java", "连接成功！");
        }
    }





}
