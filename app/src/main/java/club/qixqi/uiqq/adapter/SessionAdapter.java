package club.qixqi.uiqq.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import club.qixqi.uiqq.R;
import club.qixqi.uiqq.bean.Sessions;
import club.qixqi.uiqq.util.SessionsUtil;
import de.hdodenhof.circleimageview.CircleImageView;

public class SessionAdapter extends ArrayAdapter<Sessions> {
    private int resourceId;
    private Context mContext;

    public SessionAdapter(Context context, int textViewResourceId, List<Sessions> objects){
        super(context, textViewResourceId, objects);
        this.resourceId = textViewResourceId;
        this.mContext = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Log.e("SessionActivity.java", "???");
        Sessions sessions = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.sessionIcon = (CircleImageView) view.findViewById(R.id.session_icon);
            viewHolder.sessionName = (TextView) view.findViewById(R.id.session_name);
            viewHolder.lastContent = (TextView) view.findViewById(R.id.last_content);
            viewHolder.lastTime = (TextView) view.findViewById(R.id.last_time);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.sessionName.setText(SessionsUtil.getTheOtherName(mContext, sessions));
        viewHolder.lastContent.setText( sessions.getLastUsername() + ": " + sessions.getLastMsg());
        viewHolder.lastTime.setText(sessions.getLastTime());
        return view;
    }


    class ViewHolder{
        CircleImageView sessionIcon;
        TextView sessionName;
        TextView lastContent;
        TextView lastTime;
    }
}
