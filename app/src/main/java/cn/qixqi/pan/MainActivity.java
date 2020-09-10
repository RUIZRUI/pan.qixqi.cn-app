package cn.qixqi.pan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnClickListener, OnCheckedChangeListener {

    private Spinner spinner1;

    // 输入数据控件
    private EditText username;
    private EditText password;
    private RadioGroup sex;
    private EditText phone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        sex = (RadioGroup) findViewById(R.id.sex);
        phone = (EditText) findViewById(R.id.phone);
        sex.setOnCheckedChangeListener(this);

        // 下拉列表数据源
        spinner1 = (Spinner) this.findViewById(R.id.department);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
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
                register();
                // 提交注册信息到服务器
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
                try{
                    String sexValue = "u";  // 性别未知
                    if(sex.getCheckedRadioButtonId() == R.id.male_radio){
                        sexValue = "m";
                    }else if(sex.getCheckedRadioButtonId() == R.id.female_radio){
                        sexValue = "f";
                    }
                    URL url = new URL("https://www.qixqi.club:8443");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("username=" + username.getText() +
                            "&password=" + password.getText() +
                            "&sex=" + sexValue +
                            "&phone_num=" + phone.getText());

                    Toast.makeText(MainActivity.this, "http success", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
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
        }).run();
    }

}
