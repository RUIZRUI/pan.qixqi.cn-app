package cn.qixqi.pan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.qixqi.pan.bean.User;
import cn.qixqi.pan.context.MyApplication;
import cn.qixqi.pan.util.HttpUtil;
import cn.qixqi.pan.util.SharedPreferenceUtil;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileUserShareActivity extends AppCompatActivity implements ListView.OnItemClickListener{

    private User selfUser;
    private ListView listView;
    private List<String> shareList = new ArrayList<>();
    private ArrayAdapter<String> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_user_share);

        // 获取当前登录用户
        selfUser = SharedPreferenceUtil.getLoginUser(FileUserShareActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        changeHomeAsUp(actionBar);

        listView = (ListView) findViewById(R.id.user_share_list);
        listView.setOnItemClickListener(this);
        adapter = new ArrayAdapter<String>(FileUserShareActivity.this, android.R.layout.simple_list_item_1, shareList);
        listView.setAdapter(adapter);
        initShareList();
    }


    private void initShareList(){
        String url = this.getString(R.string.domain) + "FileShare";
        RequestBody requestBody = new FormBody.Builder()
                .add("method", "searchAll")
                .add("userId", Integer.toString(selfUser.getUserId()))
                .build();
        HttpUtil.sendOkHttpRequest(url, requestBody, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(MyApplication.getContext().getString(R.string.domain), "FileUserShareActivity: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseStr = response.body().string();
                responseWithUI(responseStr);
                Log.d(MyApplication.getContext().getString(R.string.domain), "FileUserShareActivity: " + responseStr);
            }
        });
    }


    private void responseWithUI(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject responseJson = JSON.parseObject(response);
                if(responseJson.getString("error") != null){        // 异常信息
                    Toast.makeText(FileUserShareActivity.this, responseJson.getString("error"), Toast.LENGTH_SHORT).show();
                }else{
                    shareList.clear();
                    String listStr = responseJson.getString("response");
                    List<String> list = JSON.parseArray(listStr, String.class);
                    // shareList.addAll(list);
                    int size = list.size();
                    for(int i=0; i<size; i++){
                        shareList.add(list.get(size-1-i));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toolbar_file_download, menu);
        return true;
    }


    /**
     * ListView 点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setClipboard(shareList.get(position));
        Toast.makeText(FileUserShareActivity.this, "已复制到剪贴板，快去分享吧", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(FileUserShareActivity.this, "更多", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
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


    public void setClipboard(String text){
        try {
            // 获取剪贴板管理器  真奇怪，放在上面就不行，应该又是this尚未创建，空指针异常
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(this.CLIPBOARD_SERVICE);
            // 创建普通字符型
            ClipData clipData = ClipData.newPlainText("shareUrl", text);
            clipboardManager.setPrimaryClip(clipData);
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
