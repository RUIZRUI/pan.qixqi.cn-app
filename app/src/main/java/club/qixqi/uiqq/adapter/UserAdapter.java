package club.qixqi.uiqq.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import club.qixqi.uiqq.R;
import de.hdodenhof.circleimageview.CircleImageView;

import club.qixqi.uiqq.bean.User;

public class UserAdapter extends ArrayAdapter<User> {
    private int resourceId;

    /**
     *
     * @param context
     * @param textViewResourceId
     * @param objects 类型为 List, 支持一定程度的范型
     */
    public UserAdapter(Context context, int textViewResourceId, List<User> objects){
        super(context, textViewResourceId, objects);
        this.resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.userIcon = (CircleImageView) view.findViewById(R.id.user_icon);
            viewHolder.username = (TextView) view.findViewById(R.id.user_name);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        // viewHolder.userIcon.setImageResource(the resourceId of user's icon);
        viewHolder.username.setText(user.getUsername());
        return view;
    }

    class ViewHolder{
        CircleImageView userIcon;
        TextView username;
    }

}
