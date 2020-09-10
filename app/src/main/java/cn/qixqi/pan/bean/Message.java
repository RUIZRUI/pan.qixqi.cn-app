package cn.qixqi.pan.bean;

import java.io.Serializable;

public class Message implements Serializable {
    private int msgId;      // 消息唯一id，服务器或客户端生成
    private int userId;     // 所属者，多用户登录
    private int userId1;    // 发送人id
    private String username1;   // 发送人名称
    private String userIcon1;   // 发送人头像
    private int toId;       // 接收者id
    private char chatType;  // 会话类型(群组'r'/好友'f')
    private char msgType;   // 消息类型(文字'w'/图片'p'/文件'f'/音乐'a'/系统's')
    private String msg;     // 消息内容
    private String sendTime;
    private char sendStatus;    // 发送状态(发送中'i', 发送完成'h', 发送失败'e')

    public Message(){
        super();
    }

    public Message( int userId, int userId1, String username1, String userIcon1, int toId, char chatType, char msgType, String msg, String sendTime, char sendStatus) {
        this.userId = userId;
        this.userId1 = userId1;
        this.username1 = username1;
        this.userIcon1 = userIcon1;
        this.toId = toId;
        this.chatType = chatType;
        this.msgType = msgType;
        this.msg = msg;
        this.sendTime = sendTime;
        this.sendStatus = sendStatus;
    }

    public Message(int msgId, int userId, int userId1, String username1, String userIcon1, int toId, char chatType, char msgType, String msg, String sendTime, char sendStatus) {
        this.msgId = msgId;
        this.userId = userId;
        this.userId1 = userId1;
        this.username1 = username1;
        this.userIcon1 = userIcon1;
        this.toId = toId;
        this.chatType = chatType;
        this.msgType = msgType;
        this.msg = msg;
        this.sendTime = sendTime;
        this.sendStatus = sendStatus;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId1() {
        return userId1;
    }

    public void setUserId1(int userId1) {
        this.userId1 = userId1;
    }

    public String getUsername1() {
        return username1;
    }

    public void setUsername1(String username1) {
        this.username1 = username1;
    }

    public String getUserIcon1() {
        return userIcon1;
    }

    public void setUserIcon1(String userIcon1) {
        this.userIcon1 = userIcon1;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public char getChatType() {
        return chatType;
    }

    public void setChatType(char chatType) {
        this.chatType = chatType;
    }

    public char getMsgType() {
        return msgType;
    }

    public void setMsgType(char msgType) {
        this.msgType = msgType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public char getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(char sendStatus) {
        this.sendStatus = sendStatus;
    }

    @Override
    public String toString() {
        return "MessageActivity [chatType=" + chatType + ", msg=" + msg + ", msgId=" + msgId + ", msgType=" + msgType
                + ", sendStatus=" + sendStatus + ", sendTime=" + sendTime + ", toId=" + toId + ", userIcon1="
                + userIcon1 + ", userId=" + userId + ", userId1=" + userId1 + ", username1=" + username1 + "]";
    }
}
