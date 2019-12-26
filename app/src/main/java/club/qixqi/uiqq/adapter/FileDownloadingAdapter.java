package club.qixqi.uiqq.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import club.qixqi.uiqq.R;
import club.qixqi.uiqq.bean.FileLink;

public class FileDownloadingAdapter extends BaseAdapter implements View.OnClickListener{

    private static final int TYPE_NORMAL = 0;       // 正常类型
    private Context context;
    private List<FileLink> fileLinkList;
    private Callback callback;

    public FileDownloadingAdapter(Context context, List<FileLink> fileLinkList, Callback callback) {
        super();
        this.context = context;
        this.fileLinkList = fileLinkList;
        this.callback = callback;
    }

    /**
     * 自定义接口
     */
    public interface Callback{
        public void click(View v);
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


    @Override
    public int getItemViewType(int position) {
        return TYPE_NORMAL;
    }


    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FileLink fileLink = (FileLink) getItem(position);
        View view = null;
        ViewHolder viewHolder;
        switch (getItemViewType(position)){
            case TYPE_NORMAL:
                if(convertView == null){
                    view = LayoutInflater.from(context).inflate(R.layout.file_downloading_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.fileIcon = (ImageView) view.findViewById(R.id.file_icon);
                    viewHolder.fileName = (TextView) view.findViewById(R.id.file_name);
                    viewHolder.fileMsg = (TextView) view.findViewById(R.id.file_message);
                    viewHolder.fileStatus = (ImageView) view.findViewById(R.id.file_status);
                    view.setTag(viewHolder);
                } else{
                    view = convertView;
                    viewHolder = (ViewHolder) view.getTag();
                }
                viewHolder.fileStatus.setImageResource(R.drawable.file_downloading);
                view.findViewById(R.id.layout_file_status).setOnClickListener(this);
                view.findViewById(R.id.layout_file_status).setTag(position);
                if(fileLink.getIsFolder() == 'y'){      // 文件夹
                    viewHolder.fileIcon.setImageResource(R.drawable.file_folder);
                    viewHolder.fileName.setText(fileLink.getFolderName());
                    viewHolder.fileMsg.setText(fileLink.getCreateLinkTime());
                }else {      // 文件
                    if ("apk".equals(fileLink.getFileType())) {       // apk文件
                        viewHolder.fileIcon.setImageResource(R.drawable.file_apk);
                    } else if ("audio".equals(fileLink.getFileType())) {     // 音频
                        viewHolder.fileIcon.setImageResource(R.drawable.file_audio);
                    } else if ("document".equals(fileLink.getFileType())) {       // 文档
                        viewHolder.fileIcon.setImageResource(R.drawable.file_document);
                    } else if ("picture".equals(fileLink.getFileType())) {     // 图片
                        viewHolder.fileIcon.setImageResource(R.drawable.file_picture);
                    } else if ("rar".equals(fileLink.getFileType())) {     // 压缩包
                        viewHolder.fileIcon.setImageResource(R.drawable.file_rar);
                    } else if ("unknown".equals(fileLink.getFileType())) {     // 未知类型
                        viewHolder.fileIcon.setImageResource(R.drawable.file_unknown);
                    } else if ("video".equals(fileLink.getFileType())) {       // 视频
                        viewHolder.fileIcon.setImageResource(R.drawable.file_video);
                    }
                    viewHolder.fileName.setText(fileLink.getFileName());
                    viewHolder.fileMsg.setText("文件下载中");
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
        ImageView fileStatus;
    }

    @Override
    public void onClick(View v) {
        callback.click(v);
    }
}
