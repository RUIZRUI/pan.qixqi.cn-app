package club.qixqi.uiqq.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;


import club.qixqi.uiqq.R;
import club.qixqi.uiqq.bean.FileLink;
import club.qixqi.uiqq.util.FileLinkUtil;

public class FileLinkAdapter extends BaseAdapter implements OnClickListener{

    private static final int TYPE_NORMAL = 0;       // 正常类型
    private Context context;
    private List<FileLink> fileLinkList;        // 文件列表
    private Callback callback;

    /**
     * 自定义接口，用于回调按钮点击事件到Activity
     */
    public interface Callback{
        public void click(View v);
    }


    public FileLinkAdapter(Context context,  List<FileLink> fileLinkList, Callback callback){
        // super(context, textViewResourceId, objects);
        super();
        this.context = context;
        this.fileLinkList = fileLinkList;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return fileLinkList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileLinkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * 返回布局类型
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return TYPE_NORMAL;
    }

    /**
     * 布局数量
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FileLink fileLink = (FileLink) getItem(position);
        View view = null;
        ViewHolder viewHolder;
        switch (getItemViewType(position)) {
            case TYPE_NORMAL:
                if(convertView == null){
                    view = LayoutInflater.from(context).inflate(R.layout.filelink_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.fileIcon = (ImageView) view.findViewById(R.id.file_icon);
                    viewHolder.fileName = (TextView) view.findViewById(R.id.file_name);
                    viewHolder.fileMsg = (TextView) view.findViewById(R.id.file_message);
                    viewHolder.fileCheck = (ImageView) view.findViewById(R.id.file_check);
                    view.setTag(viewHolder);
                }else{
                    view = convertView;
                    viewHolder = (ViewHolder) view.getTag();
                }
                viewHolder.fileCheck.setImageResource(R.drawable.file_uncheck);
                // viewHolder.fileCheck.setOnClickListener(this);
                view.findViewById(R.id.layout_file_check).setOnClickListener(this);
                view.findViewById(R.id.layout_file_check).setTag(position);
                if(fileLink.getIsFolder() == 'y'){      // 文件夹
                    viewHolder.fileIcon.setImageResource(R.drawable.file_folder);
                    viewHolder.fileName.setText(fileLink.getFolderName());
                    viewHolder.fileMsg.setText(fileLink.getCreateLinkTime());
                }else{      // 文件
                    if("apk".equals(fileLink.getFileType())){       // apk文件
                        viewHolder.fileIcon.setImageResource(R.drawable.file_apk);
                    }else if("audio".equals(fileLink.getFileType())){     // 音频
                        viewHolder.fileIcon.setImageResource(R.drawable.file_audio);
                    }else if("document".equals(fileLink.getFileType())){       // 文档
                        viewHolder.fileIcon.setImageResource(R.drawable.file_document);
                    }else if("picture".equals(fileLink.getFileType())){     // 图片
                        viewHolder.fileIcon.setImageResource(R.drawable.file_picture);
                    }else if("rar".equals(fileLink.getFileType())){     // 压缩包
                        viewHolder.fileIcon.setImageResource(R.drawable.file_rar);
                    }else if("unknown".equals(fileLink.getFileType())){     // 未知类型
                        viewHolder.fileIcon.setImageResource(R.drawable.file_unknown);
                    }else if("video".equals(fileLink.getFileType())){       // 视频
                        viewHolder.fileIcon.setImageResource(R.drawable.file_video);
                    }
                    viewHolder.fileName.setText(fileLink.getFileName());
                    // viewHolder.fileMsg.setText(fileLink.getCreateLinkTime() + "  " + fileLink.getFileSize() + "B");
                    viewHolder.fileMsg.setText(fileLink.getCreateLinkTime() + "  " + FileLinkUtil.getFormatSize(fileLink.getFileSize()));
                }
                break;
            default:

        }
        return view;
    }



    class ViewHolder{
        ImageView fileIcon;
        TextView fileName;
        TextView fileMsg;
        ImageView fileCheck;
    }


    /**
     * 响应点击事件，调用自定义接口，并传入View
     * @param v
     */
    @Override
    public void onClick(View v) {
        callback.click(v);
    }
}
