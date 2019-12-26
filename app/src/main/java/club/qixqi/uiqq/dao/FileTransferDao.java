package club.qixqi.uiqq.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import club.qixqi.uiqq.R;
import club.qixqi.uiqq.bean.FileLink;
import club.qixqi.uiqq.db.FileTransferDBHelper;


/**
 * 文件传输表 file_transfer 的DAO模式
 */
public class FileTransferDao {

    private FileTransferDBHelper dbHelper;

    public FileTransferDao(Context context){
        dbHelper = new FileTransferDBHelper(context, "QixQi.db", null, Integer.parseInt(context.getString(R.string.dbVersion)));
    }


    /**
     * 某条记录是否已经存在
     * @param linkId
     * @return
     */
    public boolean isContain(int linkId){
        boolean flag = false;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("file_transfer", null, "linkId = ?", new String[]{Integer.toString(linkId)}, null, null, null);
        if(cursor.moveToFirst()){
            flag = true;
        }
        db.close();
        return flag;
    }


    /**
     * 添加传输记录
     * @param fileLink
     */
    public void add(FileLink fileLink){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("linkId", fileLink.getLinkId());
        values.put("userId", fileLink.getUserId());
        values.put("fileId", fileLink.getFileId());
        values.put("fileName", fileLink.getFileName());
        values.put("fileType", fileLink.getFileType());
        values.put("fileSize", fileLink.getFileSize());
        values.put("isFolder", Character.toString(fileLink.getIsFolder()));
        values.put("folderName", fileLink.getFolderName());
        values.put("fileList", fileLink.getFileList());
        values.put("folderList", fileLink.getFolderList());
        values.put("isRoot", Character.toString(fileLink.getIsRoot()));
        values.put("parent", fileLink.getParent());
        values.put("downloadFinishTime", fileLink.getDownloadFinishTime());
        values.put("uploadFinishTime", fileLink.getUploadFinishTime());
        values.put("downloadStatus", Character.toString(fileLink.getDownloadStatus()));
        values.put("uploadStatus", Character.toString(fileLink.getUploadStatus()));
        db.insert("file_transfer", null, values);
        db.close();
    }


    /**
     * 获取某个用户的文件传输记录
     * @param userId
     * @param type          0下载 / 1上传
     * @param status        'i'下载中 / 'h' 下载完成
     * @return
     */
    public List<FileLink> getFileTransfer(final int userId, final int type, final char status){
        List<FileLink> fileTransferList = new ArrayList<>();
        String selection = "userId = ? and ";
        if(type == 0){      // 查询下载记录
            selection += "downloadStatus = ?";
        } else if(type == 1){       // 查询上传记录
            selection += "uploadStatus = ?";
        } else{
            return null;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("file_transfer", null, selection, new String[]{Integer.toString(userId), Character.toString(status)}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                int linkId = cursor.getInt(cursor.getColumnIndex("linkId"));
                // int userId1 = cursor.getInt(cursor.getColumnIndex("userId"));
                int fileId = cursor.getInt(cursor.getColumnIndex("fileId"));
                String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
                String fileType = cursor.getString(cursor.getColumnIndex("fileType"));
                long fileSize = cursor.getLong(cursor.getColumnIndex("fileSize"));
                char isFolder = cursor.getString(cursor.getColumnIndex("isFolder")).charAt(0);
                String folderName = cursor.getString(cursor.getColumnIndex("folderName"));
                String fileList = cursor.getString(cursor.getColumnIndex("fileList"));
                String folderList = cursor.getString(cursor.getColumnIndex("folderList"));
                char isRoot = cursor.getString(cursor.getColumnIndex("isRoot")).charAt(0);
                int parent = cursor.getInt(cursor.getColumnIndex("parent"));
                String downloadFinishTime = cursor.getString(cursor.getColumnIndex("downloadFinishTime"));
                String uploadFinishTime = cursor.getString(cursor.getColumnIndex("uploadFinishTime"));
                char downloadStatus = cursor.getString(cursor.getColumnIndex("downloadStatus")).charAt(0);
                char uploadStatus = cursor.getString(cursor.getColumnIndex("uploadStatus")).charAt(0);
                FileLink fileLink = new FileLink(linkId, userId, fileId, fileName, fileType, fileSize, isFolder, folderName, fileList, folderList, isRoot, parent, downloadFinishTime, uploadFinishTime, downloadStatus, uploadStatus);
                fileTransferList.add(fileLink);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return fileTransferList;
    }


    /**
     * 删除某个文件的文件下载或上传记录
     * @param linkId
     * @param type     下载0 / 上传1
     */
    public void delete(final int linkId, final int type){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String columns;
        if(type == 0){
            columns = "uploadStatus";
        }else if(type == 1){
            columns = "downloadStatus";
        }else{
            db.close();
            return;
        }
        Cursor cursor = db.query("file_transfer", new String[]{columns}, "linkId = ?", new String[]{Integer.toString(linkId)}, null, null, null);
        if(cursor.moveToFirst()){
            char status = cursor.getString(cursor.getColumnIndex(columns)).charAt(0);
            if(status == 'u'){      // 删除
                db.delete("file_transfer", "linkId = ?", new String[]{Integer.toString(linkId)});
            }else{                  // 更新
                ContentValues values = new ContentValues();
                if(type == 0){
                    values.put("downloadStatus", "u");
                }else{
                    values.put("uploadStatus", "u");
                }
                db.update("file_transfer", values, "linkId = ?", new String[]{Integer.toString(linkId)} );
            }
            db.close();
        }else{
            db.close();
            return;
        }
    }


    /**
     * 清除某个用户的所有记录
     * @param userId
     */
    public void deleteAll(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("file_transfer", "userId = ?", new String[]{Integer.toString(userId)});
        db.close();
    }


    /**
     * 更改某条记录的状态
     * @param linkId    某条记录
     * @param type      0下载/1上传
     * @param status    更改后的状态  ('i', 'h', 'u')
     */
    public void editStatus(int linkId, int type, char status){
        if(type != 0 && type != 1){
            return;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(type == 0){
            values.put("downloadStatus", Character.toString(status));
        } else{
            values.put("uploadStatus", Character.toString(status));
        }
        db.update("file_transfer", values, "linkId = ?", new String[]{Integer.toString(linkId)});
        db.close();
    }


    public void editFinishTime(int linkId, int type, String finishTime){
        if(type != 0 && type != 1){
            return;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(type == 0){
            values.put("downloadFinishTime", finishTime);
        } else{
            values.put("uploadFinishTime", finishTime);
        }
        db.update("file_transfer", values, "linkId = ?", new String[]{Integer.toString(linkId)});
    }

}
