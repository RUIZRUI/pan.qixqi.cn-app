package club.qixqi.uiqq.bean;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;

public class SearchRecord implements Serializable {
    private int id;     // 当前登录用户的id
    private int userId;
    private String username;
    private String icon;
    private String registerTime;
    private String record;
    private String recordTime;

    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SearchRecord(int id, String record, String recordTime){
        this.id = id;
        this.record = record;
        this.recordTime = recordTime;
    }

    // 扩展后的构造函数
    public SearchRecord(int id, int userId, String username, String icon, String registerTime, String record, String recordTime){
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.icon = icon;
        this.registerTime = registerTime;
        this.record = record;
        this.recordTime = recordTime;
    }

    public SearchRecord(int id, String record, Date recordTime){
        this.id = id;
        this.record = record;
        this.recordTime = df.format(recordTime);
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getUserId(){
        return this.userId;
    }

    public void setUserId(int userId){
        this.userId = userId;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getIcon(){
        return this.icon;
    }

    public void setIcon(String icon){
        this.icon = icon;
    }

    public String getRegisterTime(){
        return this.registerTime;
    }

    public void setRegisterTime(String registerTime){
        this.registerTime = registerTime;
    }

    public String getRecord(){
        return this.record;
    }

    public void setRecord(String record){
        this.record = record;
    }

    public String getRecordTime(){
        return this.recordTime;
    }

    public void setRecordTime(String recordTime){
        this.recordTime = recordTime;
    }

    public void setRecordTime(Date recordTime){
        this.recordTime = df.format(recordTime);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder searchRecord = new StringBuilder();
        searchRecord.append("id = " + id);
        searchRecord.append(", userId = " + userId);
        searchRecord.append(", username = " + username);
        searchRecord.append(", icon = " + icon);
        searchRecord.append(", registerTime = " + registerTime);
        searchRecord.append(", record = " + record);
        searchRecord.append(", recordTime = " + recordTime);
        return searchRecord.toString();
    }
}
