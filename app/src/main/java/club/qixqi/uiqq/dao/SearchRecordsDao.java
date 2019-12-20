package club.qixqi.uiqq.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.SearchEvent;
import android.widget.Toast;

import java.util.ArrayList;

import club.qixqi.uiqq.R;
import club.qixqi.uiqq.bean.SearchRecord;
import club.qixqi.uiqq.db.SearchRecordsDBHelper;

/**
 * 搜索本地记录表search_records 的DAO模式
 */
public class SearchRecordsDao {
    private SearchRecordsDBHelper dbHelper;

    public SearchRecordsDao(Context context){
        dbHelper = new SearchRecordsDBHelper(context, "QixQi.db", null, Integer.parseInt(context.getString(R.string.dbVersion)));
    }


    /**
     * 添加记录
     * todo: 实现每条记录不能重复
     */
    public long add(SearchRecord searchRecord){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", searchRecord.getId());
        values.put("userId", searchRecord.getUserId());
        values.put("username", searchRecord.getUsername());
        values.put("icon", searchRecord.getIcon());
        values.put("register_time", searchRecord.getRegisterTime());
        values.put("record", searchRecord.getRecord());
        values.put("recordTime", searchRecord.getRecordTime());
        long rowId = db.insert("search_records", null, values);
        db.close();
        return rowId;
    }

    /**
     * 获取所有记录
     */
    public ArrayList<SearchRecord> getSearchRecord(final int id){
        ArrayList<SearchRecord> record_list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("search_records", new String[]{"userId", "username", "icon", "register_time", "record", "recordTime"}, "id = ?",  new String[]{String.valueOf(id)}, null, null, "recordId desc");
        if(cursor.moveToFirst()){
            do{
                int userId = cursor.getInt(cursor.getColumnIndex("userId"));
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String icon = cursor.getString(cursor.getColumnIndex("icon"));
                String registerTime = cursor.getString(cursor.getColumnIndex("register_time"));
                String record = cursor.getString(cursor.getColumnIndex("record"));
                String recordTime = cursor.getString(cursor.getColumnIndex("recordTime"));
                SearchRecord searchRecord = new SearchRecord(id, userId, username, icon, registerTime, record, recordTime);
                record_list.add(searchRecord);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return record_list;
    }


    /**
     * 测试函数
     * @param searchRecord
     */
    public void test(SearchRecord searchRecord){
        Log.i("areyouok", "yes");
        Log.e("searchRecord = ", searchRecord.toString());
    }


    /**
     * 删除记录
     */
    public boolean delete(String record){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("search_records", "record = ?", new String[]{record});
        db.close();
        if(result > 0){
            return true;
        }else{
            return false;
        }
    }


    /**
     * 删除所有记录
     */
    public boolean deleteAll(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("search_records", null, null);
        db.close();
        if(result > 0){
            return true;
        }else{
            return false;
        }
    }
}
