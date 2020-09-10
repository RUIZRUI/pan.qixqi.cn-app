package cn.qixqi.pan.bean;

import java.io.Serializable;

public class Sessions implements Serializable {

    private static final long serialVersionUID = 1L;

    private int userId;     // 所属消息，多用户登录区分
    private int chatId;
    private int userId1;    // 发送人id
    private int userId2;    // 接收人id
    private String username1;   // 发送人名称（连接qquser表得到）
    private String username2;   // 接收人名称（连接qquser表得到）
    private String userIcon1;    // 发送人头像（连接qquser表得到）
    private String userIcon2;   // 接受人头像（连接qquser表得到）
    private String lastMsg; // 最后一条消息内容
    private String lastUsername;
    private String lastTime;
    private char lastMsgType;   // (文字'w'/图片'p'/文佳你'f'/音乐'a')
    private char chatType;      // (群组'r'/好友'f')
    private int unreadCount;    // 该会话未读消息数

    /**
     * com.alibaba.fastjson 需要
     */
    public Sessions(){
        super();
    }

    /**
     * 后台构造函数
     * @param userId
     * @param chatId
     * @param userId1
     * @param userId2
     * @param lastMsg
     * @param lastUsername
     * @param lastTime
     * @param lastMsgType
     * @param chatType
     */
    public Sessions(int userId, int chatId, int userId1, int userId2, String lastMsg, String lastUsername,
                    String lastTime, char lastMsgType, char chatType){
        this.userId = userId;
        this.chatId = chatId;
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.lastMsg = lastMsg;
        this.lastUsername = lastUsername;
        this.lastTime = lastTime;
        this.lastMsgType = lastMsgType;
        this.chatType = chatType;
    }

    /**
     * 连接 qquser表
     * @param userId
     * @param chatId
     * @param userId1
     * @param userId2
     * @param username1
     * @param username2
     * @param userIcon1
     * @param userIcon2
     * @param lastMsg
     * @param lastUsername
     * @param lastTime
     * @param lastMsgType
     * @param chatType
     */
    public Sessions(int userId, int chatId, int userId1, int userId2, String username1, String username2, String userIcon1, String userIcon2,
                    String lastMsg, String lastUsername, String lastTime, char lastMsgType, char chatType){
        this.userId = userId;
        this.chatId = chatId;   // 暂时设定为7位随机数
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.username1 = username1;
        this.username2 = username2;
        this.userIcon1 = userIcon1;
        this.userIcon2 = userIcon2;
        this.lastMsg = lastMsg;
        this.lastUsername = lastUsername;
        this.lastTime = lastTime;
        this.lastMsgType = lastMsgType;
        this.chatType = chatType;
        // this.unreadCount = unreadCount;
    }

    /**
     * 本地构造函数
     * @param userId
     * @param chatId
     * @param userId1
     * @param userId2
     * @param lastMsg
     * @param lastUsername
     * @param lastTime
     * @param lastMsgType
     * @param chatType
     * @param unreadCount
     */
    public Sessions(int userId, int chatId, int userId1, int userId2, String lastMsg, String lastUsername,
                    String lastTime, char lastMsgType, char chatType, int unreadCount){
        this.userId = userId;
        this.chatId = chatId;
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.lastMsg = lastMsg;
        this.lastUsername = lastUsername;
        this.lastTime = lastTime;
        this.lastMsgType = lastMsgType;
        this.chatType = chatType;
        this.unreadCount = unreadCount;
    }

    public int getUserId() {
        return userId;
    }

    public int getChatId() {
        return chatId;
    }

    public int getUserId1() {
        return userId1;
    }

    public int getUserId2() {
        return userId2;
    }

    public String getUsername1(){
        return username1;
    }

    public String getUsername2(){
        return username2;
    }

    public String getUserIcon1(){
        return userIcon1;
    }

    public String getUserIcon2(){
        return userIcon2;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public String getLastUsername() {
        return lastUsername;
    }

    public String getLastTime() {
        return lastTime;
    }

    public char getLastMsgType() {
        return lastMsgType;
    }

    public char getChatType() {
        return chatType;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public void setUserId1(int userId1) {
        this.userId1 = userId1;
    }

    public void setUserId2(int userId2) {
        this.userId2 = userId2;
    }

    public void setUsername1(String username1){
        this.username1 = username1;
    }

    public void setUsername2(String username2){
        this.username2 = username2;
    }

    public void setUserIcon1(String userIcon1){
        this.userIcon1 = userIcon1;
    }

    public void setUserIcon2(String userIcon2){
        this.userIcon2 = userIcon2;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public void setLastUsername(String lastUsername) {
        this.lastUsername = lastUsername;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public void setLastMsgType(char lastMsgType) {
        this.lastMsgType = lastMsgType;
    }

    public void setChatType(char chatType) {
        this.chatType = chatType;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    @Override
    public String toString() {
        return "Sessions{" +
                "userId=" + userId +
                ", chatId=" + chatId +
                ", userId1=" + userId1 +
                ", userId2=" + userId2 +
                ", username1=" + username1 +
                ", username2=" + username2 +
                ", userIcon1=" + userIcon1 +
                ", userIcon2=" + userIcon2 +
                ", lastMsg='" + lastMsg + '\'' +
                ", lastUsername='" + lastUsername + '\'' +
                ", lastTime='" + lastTime + '\'' +
                ", lastMsgType=" + lastMsgType +
                ", chatType=" + chatType +
                ", unreadCount=" + unreadCount +
                '}';
    }
}
