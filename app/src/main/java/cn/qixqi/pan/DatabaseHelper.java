package cn.qixqi.pan;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    // 建表
    public static final String CREATE_USER = "create table if not exists user(" +
            "id integer primary key," +
            "username text," +
            "password text," +
            "sex text," +
            "phone_num text," +
            "icon text default 'default.png'," +
            "birthday text default '1999-12-14')";

    private Context mContext;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_USER);
        Toast.makeText(mContext, "创建表成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists user");
        onCreate(db);
        Toast.makeText(mContext, "更新表成功", Toast.LENGTH_SHORT).show();
    }
}
