package cn.qixqi.pan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.alibaba.fastjson.JSON;

import org.jetbrains.annotations.NotNull;

import cn.qixqi.pan.adapter.SearchRecordAdapter;
import cn.qixqi.pan.bean.SearchRecord;
import cn.qixqi.pan.bean.User;
import cn.qixqi.pan.dao.SearchRecordsDao;
import cn.qixqi.pan.util.HttpUtil;
import okhttp3.Call;
import okhttp3.Response;


/**
 * todo
 * 1. 搜索框只能输入数字
 */

public class Search extends AppCompatActivity implements SearchView.OnQueryTextListener,
        BottomNavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener{

    private Toolbar toolbar;
    private SearchView searchView;
    private SearchRecordsDao searchRecordsDao;
    private ListView lvRecord;
    private List<SearchRecord> records = new ArrayList<>();         // 读取到的数据库表
    private List<SearchRecord> record_list = new ArrayList<>();     // 适配器数据源
    private SearchRecordAdapter adapter = null;
    private final int id = 801935;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // private String[] record_list2 = new String[]{"澳门", "广州", "上海", "大连", "石家庄", "济南", "王朔", "广州", "上海", "大连", "石家庄", "济南", "王朔", "广州", "上海", "大连", "石家庄", "济南", "王朔"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bnav_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        changeHomeAsUp(actionBar);      // 修改HomeAsUp图标

        initRecord();
        adapter = new SearchRecordAdapter(Search.this, R.layout.search_record_item, record_list);
        lvRecord = (ListView) findViewById(R.id.lv_record);
        lvRecord.setAdapter(adapter);
        lvRecord.setOnItemClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initRecord(){
        searchRecordsDao = new SearchRecordsDao(this);
        record_list = searchRecordsDao.getSearchRecord(id);
        // records = record_list;       // error: 此时records与record_list指向同一个列表
        records = searchRecordsDao.getSearchRecord(id);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_menu);
        // 通过 searchItem 找到 SearchView
        // searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setIconified(false);     // 搜索框直接展开显示
        // searchView.setMaxWidth(3000);    // 最大宽度
        searchView.setIconifiedByDefault(true); // 搜索图标在搜索框内
        // searchView.onActionViewExpanded();      // 没有输入内容时，没有关闭图标

        // 自动补全
        // int completeTextId = searchView.getResources().getIdentifier("android:id/search_src_text", null, null);
        // AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) searchView.findViewById(completeTextId);

        // 第一种方式，setQuery() 错误
        // AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
        // autoCompleteTextView.setThreshold(0);
        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, record_list);
        // autoCompleteTextView.setAdapter(adapter);
        // autoCompleteTextView.setOnItemClickListener(this);

        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * 重写SearchView 中的SearchAutoComplete<ListView>中的点击事件
     *
     * 现在是监听下拉列表的点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // searchView.setQuery(record_list[position], true);
        SearchRecord searchRecord = record_list.get(position);
        User user = new User(searchRecord.getUserId(), searchRecord.getUsername(), searchRecord.getIcon(), searchRecord.getRegisterTime());
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        Intent addFriendIntent = new Intent(Search.this, AddFriendActivity.class);
        addFriendIntent.putExtras(bundle);
        startActivity(addFriendIntent);
        // Toast.makeText(Search.this, user.toString(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 顶部导航栏
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
     * 底部导航栏
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.bnav_clearAll:
                // Toast.makeText(this, "删除所有历史记录", Toast.LENGTH_SHORT).show();
                // ArrayList<SearchRecord> record_list = searchRecordsDao.getSearchRecord(userId);
                StringBuilder result = new StringBuilder();
                for(int i=0; i<record_list.size(); i++){
                    result.append(record_list.get(i).toString() + "\n");
                }
                Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show();
                break;
            default:
        }
        return true;
    }

    /**
     * 输入字符时触发
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        // Toast.makeText(Search.this, "您正在输入", Toast.LENGTH_SHORT).show();
        queryRecord(newText);
        return false;
    }

    /**
     * 提交内容时触发
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(final String query) {
        // todo 需要设置搜索框只能输入数字
        String address = this.getString(R.string.domain) + "Users?method=strangerSearch&content=" + query;
        HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                showResponse(e.getMessage(), query);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                showResponse(responseData, query);
            }
        });



        // Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
//        SearchRecord searchRecord = new SearchRecord(userId, query, new Date());
//        searchRecordsDao.add(searchRecord);
        // record_list.add(searchRecord);
//        records = searchRecordsDao.getSearchRecord(userId);
        // Toast.makeText(this, "" + searchRecordsDao.add(searchRecord), Toast.LENGTH_SHORT).show();
        // searchRecordsDao.test(searchRecord);
        return true;
    }


    private void showResponse(final String response, final String query){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isJsonStr(response)){
                    User user = JSON.parseObject(response, User.class);
                    SearchRecord searchRecord = new SearchRecord(id, user.getUserId(), user.getUsername(),
                            user.getIcon(), user.getRegisterTime(), query, df.format(new Date()));
                    searchRecordsDao.add(searchRecord);
                    records = searchRecordsDao.getSearchRecord(id);
                    queryRecord(query);
                    // Toast.makeText(Search.this, searchRecord.toString(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Search.this, response + ", " + query, Toast.LENGTH_SHORT).show();
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


    /**
     * 字符串匹配
     * todo 完善字符串匹配机制
     * @param key
     */
    private void queryRecord(String key){
        Log.e("search_record", "" + records.size());
        record_list.clear();        // 清空表
        Log.e("search_record", "" + records.size());
        for(int i=0; i<records.size(); i++){
            if(records.get(i).getRecord().indexOf(key) != -1){
                record_list.add(records.get(i));
            }
        }
        adapter.notifyDataSetChanged();
    }


    private void changeHomeAsUp(ActionBar actionBar){
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);        // 隐藏标题
            // actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }
}
