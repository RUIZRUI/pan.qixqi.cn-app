package cn.qixqi.pan.bean;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int userId;
    private String password;
    private String username;
    private char sex;
    private String phone_num;
    private String icon;
    private String birthday;
    private String register_time;       // 使用String 代替 Date
    private String last_login_time;
    private String relation_time;

    public User(){
        super();
    }

    /**
     * 本地登录用户
     * @param userId
     * @param password
     * @param username
     * @param sex
     * @param phone_num
     * @param icon
     * @param birthday
     * @param register_time
     * @param last_login_time
     */
    public User(int userId, String password, String username, char sex, String phone_num,
                String icon, String birthday, String register_time, String last_login_time){
        this.userId = userId;
        this.password = password;
        this.username = username;
        this.sex = sex;
        this.phone_num = phone_num;
        this.icon = icon;
        this.birthday = birthday;
        this.register_time = register_time;
        this.last_login_time = last_login_time;
    }

    /**
     * 查找的陌生人信息
     * @param userId
     * @param username
     * @param icon
     * @param register_time
     */
    public User(int userId, String username, String icon, String register_time){
        this.userId = userId;
        this.username = username;
        this.icon = icon;
        this.register_time = register_time;
    }

    /**
     * icon, birthday 属于注册后添加信息模块
     * @param userId
     * @param username
     * @param sex
     * @param phone_num
     * @param register_time
     * @param last_login_time
     * @param relation_time
     */
    public User(int userId, String username, char sex, String phone_num, String register_time, String last_login_time, String relation_time){
        super();
        this.userId = userId;
        this.username = username;
        this.sex = sex;
        this.phone_num = phone_num;
        this.register_time = register_time;
        this.last_login_time = last_login_time;
        this.relation_time = relation_time;
    }


    public User(int userId, String username, char sex, String phone_num, String icon, String birthday, String register_time, String last_login_time, String relation_time){
        super();
        this.userId = userId;
        this.username = username;
        this.sex = sex;
        this.phone_num = phone_num;
        this.icon = icon;
        this.birthday = birthday;
        this.register_time = register_time;
        this.last_login_time = last_login_time;
        this.relation_time = relation_time;
    }


    public int getUserId(){
        return this.userId;
    }

    public void setUserId(int userId){
        this.userId = userId;
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public char getSex(){
        return this.sex;
    }

    public void setSex(char sex){
        this.sex = sex;
    }

    public String getPhoneNum(){
        return this.phone_num;
    }

    public void setPhoneNum(String phone_num){
        this.phone_num = phone_num;
    }

    public String getIcon(){
        return this.icon;
    }

    public void setIcon(String icon){
        this.icon = icon;
    }

    public String getBirthday(){
        return this.birthday;
    }

    public void setBirthday(String birthday){
        this.birthday = birthday;
    }

    public String getRegisterTime(){
        return this.register_time;
    }

    public void setRegisterTime(String register_time){
        this.register_time = register_time;
    }

    public String getLastLoginTime(){
        return this.last_login_time;
    }

    public void setLastLoginTime(String last_login_time){
        this.last_login_time = last_login_time;
    }

    public String getRelationTime(){
        return this.relation_time;
    }

    public void setRelationTime(String relation_time){
        this.relation_time = relation_time;
    }

    @NonNull
    @Override
    public String toString(){
        return "Friend{" +
                "userId=" + userId +
                ", username='" + username + "'" +
                ", sex='" + sex + "'" +
                ", phone_num=" + phone_num +
                ", icon=" + icon +
                ", birthday=" + birthday +
                ", register_time=" + register_time +
                ", last_login_time=" + last_login_time +
                ", relation_time=" + relation_time +
                "}";
    }
}
