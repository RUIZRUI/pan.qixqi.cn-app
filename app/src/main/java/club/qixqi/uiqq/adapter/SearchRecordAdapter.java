package club.qixqi.uiqq.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import club.qixqi.uiqq.R;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

import club.qixqi.uiqq.bean.SearchRecord;


public class SearchRecordAdapter extends ArrayAdapter<SearchRecord> {
    private int resourceId;

    public SearchRecordAdapter(Context context, int textViewResourceId, List<SearchRecord> objects){
        super(context, textViewResourceId, objects);
        this.resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SearchRecord searchRecord = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.recordImage = (CircleImageView) view.findViewById(R.id.record_image);
            viewHolder.recordName = (TextView) view.findViewById(R.id.record_name);
            view.setTag(viewHolder);        // 将 viewHolder 存储在 View 中
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();            // 重新获取 ViewHolder 对象
        }
        // viewHolder.recordImage.setImageResource();       // 图片根据 userId 获取
        viewHolder.recordName.setText(searchRecord.getRecord());
        return view;
    }

    class ViewHolder{
        CircleImageView recordImage;
        TextView recordName;
    }
}
