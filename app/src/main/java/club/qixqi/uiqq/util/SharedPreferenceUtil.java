package club.qixqi.uiqq.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import club.qixqi.uiqq.bean.User;
import club.qixqi.uiqq.bean.FileLink;
import club.qixqi.uiqq.context.MyApplication;


public class SharedPreferenceUtil {

    // 保存登录的用户信息
    public static void saveLoginUser(User user, Context context){
        // String name = Integer.toString(user.getUserId());
        // todo 如何保存多个用户的信息
        SharedPreferences.Editor editor = context.getSharedPreferences("qq_login_user", Context.MODE_PRIVATE).edit();
        editor.putInt("userId", user.getUserId());
        editor.putString("password", user.getPassword());
        editor.putString("username", user.getUsername());
        editor.putString("sex", Character.toString(user.getSex()));
        editor.putString("phone_num", user.getPhoneNum());
        editor.putString("icon", user.getIcon());
        editor.putString("birthday", user.getBirthday());
        editor.putString("register_time", user.getRegisterTime());
        editor.putString("last_login_time", user.getLastLoginTime());
        editor.apply();
    }

    // 获取登录的用户信息
    public static User getLoginUser(Context context){
        User user = null;
        SharedPreferences preferences = context.getSharedPreferences("qq_login_user", Context.MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);
        String password = preferences.getString("password", null);
        String username = preferences.getString("username", null);
        char sex = preferences.getString("sex", "u").toCharArray()[0];
        String phone_num = preferences.getString("phone_num", null);
        String icon = preferences.getString("icon", null);
        String birthday = preferences.getString("birthday", null);
        String register_time = preferences.getString("register_time", null);
        String last_login_time = preferences.getString("last_login_time", null);
        user = new User(userId, password, username, sex, phone_num, icon, birthday, register_time, last_login_time);
        return user;
    }


    // 获取登录用户的id
    public static int getUserId(Context context){
        SharedPreferences preferences = context.getSharedPreferences("qq_login_user", Context.MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);
        return userId;
    }


    // 保存到当前登录用户的根文件信息
    public static void saveRootFolder(FileLink fileLink){
        Log.d("login1", fileLink.toString());
        Context context = MyApplication.getContext();
        SharedPreferences.Editor editor = context.getSharedPreferences("qq_root_folder", Context.MODE_PRIVATE).edit();
        editor.putInt("linkId", fileLink.getLinkId());
        editor.putInt("userId", fileLink.getUserId());
        editor.putInt("fileId", fileLink.getFileId());
        editor.putString("fileName", fileLink.getFileName());
        editor.putString("fileType", fileLink.getFileType());
        editor.putLong("fileSize", fileLink.getFileSize());
        editor.putString("isFolder", Character.toString(fileLink.getIsFolder()));
        editor.putString("folderName", fileLink.getFolderName());
        editor.putString("fileList", fileLink.getFileList());
        editor.putString("folderList", fileLink.getFolderList());
        editor.putString("isRoot", Character.toString(fileLink.getIsRoot()));
        editor.putInt("parent", fileLink.getParent());
        editor.putString("createLinkTime", fileLink.getCreateLinkTime());
        editor.apply();
    }

    // 获取当前登录用户的根文件信息
    public static FileLink getRootFolder(){
        FileLink fileLink = null;
        Context context = MyApplication.getContext();
        SharedPreferences preferences = context.getSharedPreferences("qq_root_folder", Context.MODE_PRIVATE);
        int linkId = preferences.getInt("linkId", -1);
        int userId = preferences.getInt("userId", -1);
        int fileId = preferences.getInt("fileId", -1);
        String fileName = preferences.getString("fileName", null);
        String fileType = preferences.getString("fileType", null);
        long fileSize = preferences.getLong("fileSize", -1);
        char isFolder = preferences.getString("isFolder", "n").toCharArray()[0];
        String folderName = preferences.getString("folderName", null);
        String fileList = preferences.getString("fileList", null);
        String folderList = preferences.getString("folderList", null);
        char isRoot = preferences.getString("isRoot", "n").charAt(0);
        int parent = preferences.getInt("parent", -1);
        String createLinkTime = preferences.getString("createLinkTime", null);
        fileLink = new FileLink(linkId, userId, fileId, fileName, fileType, fileSize, isFolder, folderName, fileList, folderList, isRoot, parent, createLinkTime);
        return fileLink;
    }


    // 更改当前登录用户根文件夹信息
    public static void updateFileList(String fileList){
        Context context = MyApplication.getContext();
        SharedPreferences.Editor editor = context.getSharedPreferences("qq_root_folder", Context.MODE_PRIVATE).edit();
        editor.putString("fileList", fileList);
        editor.apply();
    }


    // 更改当前登录用户根文件夹信息
    public static void updateFolderList(String folderList){
        Context context = MyApplication.getContext();
        SharedPreferences.Editor editor = context.getSharedPreferences("qq_root_folder", Context.MODE_PRIVATE).edit();
        editor.putString("folderList", folderList);
        editor.apply();
    }



}
