package club.qixqi.uiqq;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

import club.qixqi.uiqq.bean.User;
import club.qixqi.uiqq.util.SharedPreferenceUtil;

public class register extends AppCompatActivity implements OnClickListener, OnCheckedChangeListener {

    private Spinner spinner1;

    // 输入数据控件
    private EditText username;
    private EditText password;
    private RadioGroup sex;
    private EditText phone;

    private DatabaseHelper dbHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this, "QixQi.db", null, Integer.parseInt(this.getString(R.string.dbVersion)));

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        sex = (RadioGroup) findViewById(R.id.sex);
        phone = (EditText) findViewById(R.id.phone);
        sex.setOnCheckedChangeListener(this);

        // 下拉列表数据源
        spinner1 = (Spinner) this.findViewById(R.id.department);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(register.this,
                android.R.layout.simple_spinner_item, getDataSource());
        spinner1.setAdapter(adapter);

        Button register_button = (Button) this.findViewById(R.id.register);
        register_button.setOnClickListener(this);

        // 隐藏密码
        EditText password = (EditText) findViewById(R.id.password);
        // 显示密码
        // password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        // 隐藏密码
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());

    }

    public List<String> getDataSource(){
        List<String> list = new ArrayList<String>();
        list.add("销售部");
        list.add("技术部");
        list.add("人事部");
        list.add("财务部");
        return list;
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.register:
                // 点击注册按钮事件
                // Intent intent = new Intent(MainActivity.this, login.class);
                // startActivity(intent);
                // register();
                // 提交注册信息到服务器
                register();

                // 保存到本地
                int id = userRegister();
                Toast.makeText(register.this, "注册成功，账号为: " + id, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId){
        // RadioButton sexbtn = (RadioButton) findViewById(checkedId);
    }


    private void register(){
        // 开启线程发送网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                DataOutputStream out = null;
                BufferedReader reader = null;
                try{
                    String sexValue = "u";  // 性别未知
                    if(sex.getCheckedRadioButtonId() == R.id.male_radio){
                        sexValue = "m";
                    }else if(sex.getCheckedRadioButtonId() == R.id.female_radio){
                        sexValue = "f";
                    }
                    URL url = new URL("https://www.ourvultr.club:8443/qq/Register");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8 * 000);
                    connection.setReadTimeout(8 * 000);
                    out = new DataOutputStream(connection.getOutputStream());
                    String requestMsg = "username=" + username.getText() +
                            "&password=" + password.getText() +
                            "&sex=" + sexValue +
                            "&phone_num=" + phone.getText();
                    out.write(requestMsg.getBytes());       // 处理中文乱码
//                    out.writeBytes("username=" + username.getText() +
//                            "&password=" + password.getText() +
//                            "&sex=" + sexValue +
//                            "&phone_num=" + phone.getText());


                    // 不读取数据，就提交不上，好傻
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder responseMsg = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null){
                        responseMsg.append(line);
                    }
                    Log.d("post success", "注册成功");
                    showResponseMsg(responseMsg.toString());

                    // Toast.makeText(register.this, "http success", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.e("凉凉", "凉凉");
                    e.printStackTrace();
                    Log.e("大错特错 ", "" + e);
                } finally {
                    if(reader != null){
                        try{
                            reader.close();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    if(out != null){
                        try{
                            out.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    if(connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }


    private void showResponseMsg(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Toast.makeText(register.this, response, Toast.LENGTH_SHORT).show();
                if(isJsonStr(response)){
                    User user = JSON.parseObject(response, User.class);
                    SharedPreferenceUtil.saveLoginUser(user, register.this);
                    Toast.makeText(register.this, "注册成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(register.this, response, Toast.LENGTH_SHORT).show();
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


    private int userRegister(){
        // 账号：产生6位随机数
        int id = (int)((Math.random()*9+1)*100000);
        String username1 = username.getText().toString();
        String password1 = password.getText().toString();
        String sexValue = "u";  // 性别未知
        if(sex.getCheckedRadioButtonId() == R.id.male_radio){
            sexValue = "m";
        }else if(sex.getCheckedRadioButtonId() == R.id.female_radio){
            sexValue = "f";
        }
        String phone_num = phone.getText().toString();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        // 组装数据
        values.put("id", id);
        values.put("username", username1);
        values.put("password", password1);
        values.put("sex", sexValue);
        values.put("phone_num", phone_num);
        db.insert("user", null, values);
        return id;
    }

}
