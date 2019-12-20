package club.qixqi.uiqq.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import club.qixqi.uiqq.R;
import club.qixqi.uiqq.bean.Message;
import club.qixqi.uiqq.bean.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends BaseAdapter {

    private static final int TYPE_ERROR = 0;        // 未知消息
    private static final int TYPE_SYSTEM = 1;       // 系统提示消息
    private static final int TYPE_SEND = 2;         // 发送文本消息
    private static final int TYPE_RECEIVE = 3;      // 接收文本消息
    private Context context;
    private List<Message> messageList;      // 消息列表
    private User selfUser;      // 当前登录的用户

    public MessageAdapter(Context context, List<Message> messageList, User selfUser){
        super();        // 继承自BaseAdapter，只有这一个构造函数
        // Log.e("SessionActivity.java", messageList.toString());
        // Log.e("SessionActivity.java", selfUser.toString());
        this.context = context;
        this.messageList = messageList;
        this.selfUser = selfUser;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 返回当前布局类型
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        // Log.e("SessionActivity.java", messageList.get(position).toString());
        // 系统提示消息
        if(messageList.get(position).getMsgType() == 's') {
            return TYPE_SYSTEM;
        }
        // 文本消息
        if(messageList.get(position).getMsgType() == 'w'){
            // 发送消息
            if(messageList.get(position).getUserId1() == selfUser.getUserId()) {
                return TYPE_SEND;
            }else{
                return TYPE_RECEIVE;
            }
        }
        return TYPE_ERROR;

    }

    /**
     * 返回布局种类的数量
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return 4;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Log.e("SessionActivity.java", getItemViewType(position)+"");
        ErrorViewHolder errorViewHolder;
        SystemViewHolder systemViewHolder;
        SendViewHolder sendViewHolder;
        ReceiveViewHolder receiveViewHolder;
        // Log.e("123456789", " " + getItemViewType(position));
        switch (getItemViewType(position)){
            case TYPE_ERROR:
                // 错误消息
                if(convertView == null){
                    convertView = LayoutInflater.from(context).inflate(R.layout.message_error_item, null);
                    errorViewHolder = new ErrorViewHolder();
                    errorViewHolder.errorMessage = (TextView) convertView.findViewById(R.id.error_message);
                    convertView.setTag(errorViewHolder);
                }else{
                    errorViewHolder = (ErrorViewHolder) convertView.getTag();
                }
                errorViewHolder.errorMessage.setText("error");
                break;
            case TYPE_SYSTEM:
                // 系统提示消息
                if(convertView == null){
                    convertView = LayoutInflater.from(context).inflate(R.layout.message_system_item, null);
                    systemViewHolder = new SystemViewHolder();
                    systemViewHolder.systemMessage = (TextView) convertView.findViewById(R.id.system_message);
                    convertView.setTag(systemViewHolder);
                }else{
                    systemViewHolder = (SystemViewHolder) convertView.getTag();
                }
                systemViewHolder.systemMessage.setText(messageList.get(position).getMsg());
                break;
            case TYPE_SEND:
                // 发送文本消息
                if(convertView == null){
                    convertView = LayoutInflater.from(context).inflate(R.layout.message_send_item, null);
                    sendViewHolder = new SendViewHolder();
                    sendViewHolder.userIcon = (CircleImageView) convertView.findViewById(R.id.user_icon);
                    sendViewHolder.userMessage = (TextView) convertView.findViewById(R.id.user_message);
                    convertView.setTag(sendViewHolder);
                }else{
                    sendViewHolder = (SendViewHolder) convertView.getTag();
                }
                sendViewHolder.userMessage.setText(messageList.get(position).getMsg());
                break;
            case TYPE_RECEIVE:
                // 接收文本消息
                if(convertView == null){
                    convertView = LayoutInflater.from(context).inflate(R.layout.message_receive_item, null);
                    receiveViewHolder = new ReceiveViewHolder();
                    receiveViewHolder.userIcon = (CircleImageView) convertView.findViewById(R.id.user_icon);
                    receiveViewHolder.userMessage = (TextView) convertView.findViewById(R.id.user_message);
                    convertView.setTag(receiveViewHolder);
                }else{
                    receiveViewHolder = (ReceiveViewHolder) convertView.getTag();
                }
                receiveViewHolder.userMessage.setText(messageList.get(position).getMsg());
                break;
        }
        return convertView;
    }

    // 错误信息处理布局
    class ErrorViewHolder{
        TextView errorMessage;
    }

    // 系统提示消息布局
    class SystemViewHolder{
        TextView systemMessage;
    }

    // 发送文本消息布局
    class SendViewHolder{
        TextView userMessage;
        CircleImageView userIcon;
    }

    // 接收文本消息布局
    class ReceiveViewHolder{
        CircleImageView userIcon;
        TextView userMessage;
    }


    /**
     * 表明adapter中的所有item是否可以点击
     * @return
     */
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    /**
     * 表明下标为position的item不可选中，不可点击
     * @param position
     * @return
     */
    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
