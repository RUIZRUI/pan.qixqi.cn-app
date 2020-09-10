package cn.qixqi.pan.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class FileTransferDBHelper extends SQLiteOpenHelper {

    // 建立文件传输记录表（上传、下载）
    public static final String CREATE_FILETRANSFER = "create table if not exists file_transfer(" +
            "linkId integer primary key autoincrement, " +
            "userId integer," +
            "fileId integer," +
            "fileName text," +
            "fileType text," +
            "fileSize integer," +
            "isFolder text," +
            "folderName text," +
            "fileList text," +
            "folderList text," +
            "isRoot text," +
            "parent integer," +
            "downloadFinishTime text," +            // 下载完成时间
            "uploadFinishTime text," +              // 上传完成时间
            "downloadStatus text," +
            "uploadStatus text)";

    private Context mContext;

    public FileTransferDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FILETRANSFER);
        Toast.makeText(mContext, "创建文件传输表成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists file_transfer");
        onCreate(db);
        Toast.makeText(mContext, "更新文件传输表成功", Toast.LENGTH_SHORT).show();
    }
}
