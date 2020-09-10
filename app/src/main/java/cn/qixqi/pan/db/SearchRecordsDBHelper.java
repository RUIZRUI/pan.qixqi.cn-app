package cn.qixqi.pan.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class SearchRecordsDBHelper extends SQLiteOpenHelper {

    // 建立搜索历史记录表
    public static  final String CREATE_SEARCHRECORDS = "create table if not exists search_records(" +
            "recordId integer primary key autoincrement, " +
            "id integer," +     // 现在登录用户的id，多用户登录时区分
            "userId integer, " +
            "username text," +
            "icon text," +
            "register_time text," +
            "record text," +
            "recordTime text)";


    private Context mContext;

    public SearchRecordsDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_SEARCHRECORDS);
        Toast.makeText(mContext, "创建搜索历史表成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists search_records");
        onCreate(db);
        Toast.makeText(mContext, "更新搜索历史表成功", Toast.LENGTH_SHORT).show();
    }
}
