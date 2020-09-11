package cn.qixqi.pan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.qixqi.pan.bean.FileLink;
import cn.qixqi.pan.bean.User;
import cn.qixqi.pan.context.MyApplication;
import cn.qixqi.pan.util.HttpUtil;
import cn.qixqi.pan.util.SharedPreferenceUtil;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class login extends AppCompatActivity implements View.OnClickListener {

    private DatabaseHelper dbHelper;
    private int userId;
    private EditText idText;
    private EditText passText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 登录跳转到主页面
        isLogined();

        dbHelper = new DatabaseHelper(this, "QixQi.db", null, Integer.parseInt(this.getString(R.string.dbVersion)));

        idText = (EditText) findViewById(R.id.username);
        passText = (EditText) findViewById(R.id.password);

        Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(this);

        Button finishButton = (Button) findViewById(R.id.finish);
        finishButton.setOnClickListener(this);

        TextView non = (TextView) findViewById(R.id.non);
        non.setOnClickListener(this);

        TextView news = (TextView) findViewById(R.id.news);
        news.setOnClickListener(this);

        TextView tiktok = (TextView) findViewById(R.id.tiktok);
        tiktok.setOnClickListener(this);

        // 隐藏密码
        EditText password = (EditText) findViewById(R.id.password);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());

    }


    /**
     * 判断是否已经登录
     * @return
     */
    private void isLogined(){
        User loginUser = SharedPreferenceUtil.getLoginUser(login.this);
        if(loginUser.getUserId() != -1){
            Intent successIntent = new Intent(login.this, FileActivity.class);
            successIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(successIntent);
        }
    }


    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.login:
                // Intent intent = new Intent(login.this, success.class);
                // startActivity(intent);
                // Toast.makeText(login.this, "用户点击了登录", Toast.LENGTH_SHORT).show();

                // 登录到后台
                login(idText.getText().toString(), passText.getText().toString());

                /*if(userLogin()){
                    Intent success_intent = new Intent(login.this, success.class);
                    startActivity(success_intent);
                }else{
                    idText.setText("");
                    passText.setText("");
                    Toast.makeText(login.this, "登录失败，账号或密码错误", Toast.LENGTH_SHORT).show();
                }*/
                break;
            case R.id.finish:
                finish();
                break;
            case R.id.non:
                Toast.makeText(login.this, "你为什么无法登录呀", Toast.LENGTH_SHORT).show();
                break;
            case R.id.news:
                Intent register_intent = new Intent(login.this, register.class);
                startActivity(register_intent);
                break;
            case R.id.tiktok:
                Intent tiktok_intent = new Intent(login.this, TikTok.class);
                startActivity(tiktok_intent);
                break;
        }
    }

    private void login(final String id, final String password){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader reader = null;
                DataOutputStream out = null;
                try{
                    URL url = new URL(MyApplication.getContext().getString(R.string.domain) + "Login");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(8 * 1000);
                    conn.setReadTimeout(8 * 1000);
                    out = new DataOutputStream(conn.getOutputStream());
                    String requestMsg = "id=" + id + "&password=" + password;
                    out.write(requestMsg.getBytes());       // 处理中文乱码

                    // 获取并处理响应数据
                    InputStream in = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder responseMsg = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null){
                        responseMsg.append(line);
                    }
                    showResponseMsg(responseMsg.toString());
                    Log.i("post success", "登录成功");
                } catch (Exception e){
                    Log.e("凉凉", "" + e);
                    e.printStackTrace();
                } finally {
                    if(out != null){
                        try{
                            out.close();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    if(reader != null){
                        try{
                            reader.close();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    if(conn != null){
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }


    /**
     * 获取根文件夹链接
     * @param userId
     */
    private void getRootFolder(int userId){
        String address = this.getString(R.string.domain) + "FileSearch";
        final RequestBody requestBody = new FormBody.Builder()
                .add("method", "getRootFolder")
                .add("userId", Integer.toString(userId))
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("login1", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseStr = response.body().string();      // 这个狗玩意，自己关闭
                Log.d("login1", responseStr);
                JSONObject responseJSon = JSON.parseObject(responseStr.trim());
                if(responseJSon.containsKey("rootFolder")){
                    JSONObject rootJson =  responseJSon.getJSONObject("rootFolder");
                    Log.d("login1", JSON.toJSONString(rootJson));
                    FileLink fileLink = JSON.toJavaObject(rootJson, FileLink.class);
                    SharedPreferenceUtil.saveRootFolder(fileLink);
                    Intent intent = new Intent(login.this, FileActivity.class);
                    startActivity(intent);
                }else{
                    Log.e("login1", "未获取到当前登录用户的根文件夹链接");
                }
            }
        });
    }


    private void showResponseMsg(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Toast.makeText(login.this, response, Toast.LENGTH_SHORT).show();
                if(isJsonStr(response)){
                    // Log.d("login.java", response);
                    User user = JSON.parseObject(response, User.class);
                    // Log.d("login.java", user.toString());
                    SharedPreferenceUtil.saveLoginUser(user, login.this);
                    getRootFolder(user.getUserId());
                    Toast.makeText(login.this, "登录成功", Toast.LENGTH_SHORT).show();
                    // User user2 = SharedPreferenceUtil.getLoginUser(login.this);
                    // Log.d("login.java", "读取到的: " + user2.toString());
                }else{
                    Toast.makeText(login.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 判断是否是json格式的字符串
     * @param response
     * @return
     */
    private boolean isJsonStr(final String response){
        try{
            User user = JSON.parseObject(response, User.class);
            if(user != null){
                return true;
            }
            return false;
        } catch (Exception e){
            return false;
        }
    }


    private boolean userLogin(){
        String id = idText.getText().toString();
        String password = passText.getText().toString();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("user", null, "id = ? and password = ?",
                new String[]{id, password}, null, null, null);
        if(cursor.moveToFirst()){
            return true;
        }else{
            return false;
        }
    }

}
