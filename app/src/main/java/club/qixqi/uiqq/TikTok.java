package club.qixqi.uiqq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.text.style.AbsoluteSizeSpan;

public class TikTok extends AppCompatActivity implements OnClickListener{

    private EditText tiktok_phone;
    private Button cancel;
    private ImageView tiktok_login;
    private ImageView qq_icon;
    private ImageView wechat_icon;
    private ImageView weibo_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tik_tok);

        tiktok_phone = (EditText) findViewById(R.id.tiktok_phone);

        // 修改 EditText 提示信息大小
        editHint((EditText) findViewById(R.id.tiktok_phone));

        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        tiktok_login = (ImageView) findViewById(R.id.tiktok_login);
        tiktok_login.setOnClickListener(this);
        qq_icon = (ImageView) findViewById(R.id.qq_icon);
        qq_icon.setOnClickListener(this);
        wechat_icon = (ImageView) findViewById(R.id.wechat_icon);
        wechat_icon.setOnClickListener(this);
        weibo_icon = (ImageView) findViewById(R.id.weibo_icon);
        weibo_icon.setOnClickListener(this);


    }


    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.cancel:
                finish();
                break;
            case R.id.tiktok_login:
                if(checkPhoneNum()){
                    Toast.makeText(TikTok.this, "您要登录啦！", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(TikTok.this, "您输对再点我行不行", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.qq_icon:
                Intent qqLogin_intent = new Intent(TikTok.this, login.class);
                startActivity(qqLogin_intent);
                break;
            case R.id.wechat_icon:
                Toast.makeText(TikTok.this, "您选择微信登录", Toast.LENGTH_SHORT).show();
                break;
            case R.id.weibo_icon:
                Toast.makeText(TikTok.this, "您选择微博登录", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    // 修改EditText 中 hint大小
    private void editHint(EditText editText){
        switch(editText.getId()){
            case R.id.tiktok_phone:
                SpannableString phone_num = new SpannableString(editText.getHint());
                AbsoluteSizeSpan textSize = new AbsoluteSizeSpan(20, true);
                phone_num.setSpan(textSize, 0, phone_num.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                editText.setHint(phone_num);
                break;
        }
    }


    // 检查 phone_num 格式
    private boolean checkPhoneNum(){
        String phoneNum = tiktok_phone.getText().toString();
        if(phoneNum.length() < 11){
            return false;
        }else if("1".equals(phoneNum.substring(0, 1))){        // 这里应该判断前三位，即手机号码段，但手机号码段需要根据实际不断更新，这里简单判断一下
            return true;
        }
        return false;
    }
}
